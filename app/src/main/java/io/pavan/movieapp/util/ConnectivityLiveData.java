package io.pavan.movieapp.util;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by pavan on 02/12/18
 */
public class ConnectivityLiveData extends MutableLiveData<Boolean> {

    private static final int NETWORK_CONNECTIVITY = 1;

    private NetworkConnectivityListener mNetworkConnectivityListener;

    private Context mContext;
    private Handler mHandler;

    public ConnectivityLiveData(Context context) {
        mContext = context;

        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                return ConnectivityLiveData.this.handleMessage(msg);
            }
        });
    }

    @Override
    protected void onActive() {
        super.onActive();
        registerNetworkConnectivityListener();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        unregisterNetConnectivityListener();
    }

    private void registerNetworkConnectivityListener() {
        mNetworkConnectivityListener = new NetworkConnectivityListener();
        mNetworkConnectivityListener.startListening(mContext);
        mNetworkConnectivityListener.registerHandler(mHandler, NETWORK_CONNECTIVITY);
    }

    private void unregisterNetConnectivityListener() {
        if (mNetworkConnectivityListener != null) {
            mNetworkConnectivityListener.unregisterHandler(mHandler);
            mNetworkConnectivityListener.stopListening();
        }
    }

    private boolean handleMessage(Message message) {
        switch (message.what) {
        case NETWORK_CONNECTIVITY:
            setValue(mNetworkConnectivityListener.isNetworkAvailable());
            break;
        }

        return false;
    }
}
