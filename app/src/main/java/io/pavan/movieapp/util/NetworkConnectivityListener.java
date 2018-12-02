/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pavan.movieapp.util;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

import io.pavan.movieapp.arch.Injector;


/**
 * A wrapper for a broadcast receiver that provides network connectivity
 * state information, independent of network type (mobile, Wi-Fi, etc.).
 * {@hide}
 */
public class NetworkConnectivityListener {

    private static final String TAG = "NetConnectivityListener";
    private static final boolean DBG = false;

    private Context mContext;
    private HashMap<Handler, Integer> mHandlers = new HashMap<Handler, Integer>();
    private State mState;
    private boolean mListening;
    private String mReason;
    private boolean mIsFailover;

    /** Network connectivity information */
    private NetworkInfo mNetworkInfo;

    /**
     * In case of a Disconnect, the connectivity manager may have
     * already established, or may be attempting to establish, connectivity
     * with another network. If so, {@code mOtherNetworkInfo} will be non-null.
     */
    private NetworkInfo mOtherNetworkInfo;

    private ConnectivityBroadcastReceiver mReceiver;

    private INetworkCallback mConnectionStateMonitor;

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {

		@Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || !mListening) {
                Log.w(TAG, "onReceived() called with " + mState.toString() + " and " + intent);
                return;
            }

            boolean noConnectivity =
                intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (noConnectivity) {
                mState = State.NOT_CONNECTED;
            }
            else {
                mState = State.CONNECTED;
            }

            mOtherNetworkInfo = (NetworkInfo)
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            mIsFailover =
                    intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                mNetworkInfo = cm.getActiveNetworkInfo();
            }
            else {
                mNetworkInfo = (NetworkInfo)
                        intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            }

            mState = mNetworkInfo != null && mNetworkInfo.isConnected() ?
                    State.CONNECTED : State.NOT_CONNECTED;

            if (DBG) {
                Log.d(TAG, "onReceive(): mNetworkInfo=" + mNetworkInfo +  " mOtherNetworkInfo = "
                        + (mOtherNetworkInfo == null ? "[none]" : mOtherNetworkInfo +
                        " noConn=" + noConnectivity) + " mState=" + mState.toString());
            }

            checkForInternet();
            notifyHandlers();
        }
    }

    private void checkForInternet() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                boolean isInternetOn = isInternetAvailable();
                if (isInternetOn) {
                    mState = State.CONNECTED_TO_INTERNET;
                }
                notifyHandlers();
                handler.removeCallbacks(this);
            }
        });
    }

    private void notifyHandlers() {
        // Notifiy any handlers.
        Iterator<Handler> it = mHandlers.keySet().iterator();
        while (it.hasNext()) {
            Handler target = it.next();
            Message message = Message.obtain(target, mHandlers.get(target));
            target.sendMessage(message);
        }
    }

    public enum State {
        UNKNOWN,

        /** This state is returned if there is connectivity to any network **/
        CONNECTED,

        /** This state is returned if three is internet connectivity on current network*/
        CONNECTED_TO_INTERNET,

        /**
         * This state is returned if there is no connectivity to any network. This is set
         * to true under two circumstances:
         * <ul>
         * <li>When connectivity is lost to one network, and there is no other available
         * network to attempt to switch to.</li>
         * <li>When connectivity is lost to one network, and the attempt to switch to
         * another network fails.</li>
         */
        NOT_CONNECTED
    }

    /**
     * Create a new NetworkConnectivityListener.
     */
    public NetworkConnectivityListener() {
        mState = State.UNKNOWN;
        mReceiver = new ConnectivityBroadcastReceiver();

        getConnectionMonitor();
    }

    private INetworkCallback getConnectionMonitor() {
        if (mConnectionStateMonitor != null) {
            return mConnectionStateMonitor;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                mConnectionStateMonitor = new ConnectionStateMonitor(this);
            } catch (Exception e) {
                mConnectionStateMonitor = new DummyNetworkCallback();
                e.printStackTrace();
            }
        }
        else {
            mConnectionStateMonitor = new DummyNetworkCallback();
        }

        return mConnectionStateMonitor;
    }

    /**
     * This method starts listening for network connectivity state changes.
     * @param context
     */
    public synchronized void startListening(Context context) {
        if (!mListening) {
            mContext = context;

            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(mReceiver, filter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getConnectionMonitor().enable(mContext);
            }
            mListening = true;
        }
    }

    /**
     * This method stops this class from listening for network changes.
     */
    public synchronized void stopListening() {
        if (mListening) {
            getConnectionMonitor().disable(mContext);
            mContext.unregisterReceiver(mReceiver);
            mContext = null;
            mNetworkInfo = null;
            mOtherNetworkInfo = null;
            mIsFailover = false;
            mReason = null;
            mListening = false;
        }
    }

    /**
     * This methods registers a Handler to be called back onto with the specified what code when
     * the network connectivity state changes.
     *
     * @param target The target handler.
     * @param what The what code to be used when posting a message to the handler.
     */
    public void registerHandler(Handler target, int what) {
        mHandlers.put(target, what);
    }

    /**
     * This methods unregisters the specified Handler.
     * @param target
     */
    public void unregisterHandler(Handler target) {
        mHandlers.remove(target);
    }

    public State getState() {
        return mState;
    }

    /**
     * Return the NetworkInfo associated with the most recent connectivity event.
     * @return {@code NetworkInfo} for the network that had the most recent connectivity event.
     */
    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    /**
     * If the most recent connectivity event was a DISCONNECT, return
     * any information supplied in the broadcast about an alternate
     * network that might be available. If this returns a non-null
     * value, then another broadcast should follow shortly indicating
     * whether connection to the other network succeeded.
     *
     * @return NetworkInfo
     */
    public NetworkInfo getOtherNetworkInfo() {
        return mOtherNetworkInfo;
    }

    /**
     * Returns true if the most recent event was for an attempt to switch over to
     * a new network following loss of connectivity on another network.
     * @return {@code true} if this was a failover attempt, {@code false} otherwise.
     */
    public boolean isFailover() {
        return mIsFailover;
    }

    /**
     * An optional reason for the connectivity state change may have been supplied.
     * This returns it.
     * @return the reason for the state change, if available, or {@code null}
     * otherwise.
     */
    public String getReason() {
        return mReason;
    }
    
    public boolean isNetworkAvailable() {
    	boolean isNetworkAvailable = (mState.equals(State.CONNECTED) ||
                mState.equals(State.CONNECTED_TO_INTERNET));

        if (!isNetworkAvailable) {

            if (mContext == null) {
                mContext = Injector.instance().getApplication().getApplicationContext();
            }
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            isNetworkAvailable = isNetworkAvailable(cm);
            mState = isNetworkAvailable ? State.CONNECTED : State.NOT_CONNECTED;

            if (isNetworkAvailable) {
                isNetworkAvailable = isInternetAvailable();
                if (isNetworkAvailable) {
                    mState = State.CONNECTED_TO_INTERNET;
                }
            }
        }
        return isNetworkAvailable;
    }
    
    public static boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		boolean isConnected = networkInfo != null && networkInfo.isConnected();

		if (isConnected) {
            if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                NetworkInfo n;
                for (Network network : networks) {
                    n = connectivityManager.getNetworkInfo(network);
                    if (n != null && isConnected(n)) {
                        return true;
                    }
                }
            }
            else {
                NetworkInfo[] networks = connectivityManager.getAllNetworkInfo();
                for (NetworkInfo n : networks) {
                    if (n != null && isConnected(n)) {
                        return true;
                    }
                }
            }
		}

		return false;
    }

    private static boolean isConnected(NetworkInfo n) {
        if (n.getState() == NetworkInfo.State.CONNECTED
                || n.getState() == NetworkInfo.State.CONNECTING) {
            if (n.getType() == ConnectivityManager.TYPE_MOBILE
                    || n.getType() == ConnectivityManager.TYPE_WIFI
                    || n.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return true;
            }
        }
        return false;
    }

    public int getConnectedNetworkType() {
        return isNetworkAvailable() && mNetworkInfo != null ? mNetworkInfo.getType() : -1;
    }

//    https://stackoverflow.com/questions/9570237/android-check-internet-connection
    public boolean isInternetAvailable() {
        if (mState.equals(State.CONNECTED_TO_INTERNET)) {
            return true;
        }

        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.getHostName().equals("");
        } catch (Exception e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback
            implements INetworkCallback {

        final NetworkRequest networkRequest;
        private NetworkConnectivityListener callback;

        public ConnectionStateMonitor(NetworkConnectivityListener networkConnectivityListener) {
            callback = networkConnectivityListener;

            networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
        }

        public void enable(Context context) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerNetworkCallback(networkRequest , this);
        }

        public void disable(Context context) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(this);
        }

        @Override
        public void onAvailable(Network network) {
            callback.onNetworkAvailable(network);
        }
    }

    private void onNetworkAvailable(Network network) {
        if (mContext == null) {

            if (Injector.instance() == null || Injector.instance().getApplication() == null) {
                return;
            }

            mContext = Injector.instance().getApplication();
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        mState = mNetworkInfo != null && mNetworkInfo.isConnected() ?
                State.CONNECTED : State.NOT_CONNECTED;

        checkForInternet();
        notifyHandlers();
    }

    private class DummyNetworkCallback implements INetworkCallback {

        @Override
        public void enable(Context context) {

        }

        @Override
        public void disable(Context context) {

        }
    }

    private interface INetworkCallback {

        void enable(Context context);

        void disable(Context context);
    }


}

