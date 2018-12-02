package io.pavan.movieapp.arch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import io.pavan.movieapp.util.ConnectivityLiveData;

/**
 * Created by pavan on 02/12/18
 */
@SuppressLint("Registered")
public class LifeCycleAwareApplication extends Application {

    private ActivityStackObserver mActivityStackObserver;

    private ConnectivityLiveData mConnectivityObservable;
    private Observer<Boolean> mConnectivityObserver;

    public LifeCycleAwareApplication() {
        mActivityStackObserver = new ActivityStackObserver();
        Injector.instance().setApplication(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        monitorConnectivityChange();
    }

    public Activity currentActivity() {
        return mActivityStackObserver.getCurrentActivity();
    }

    public Activity previousActivity() {
        return mActivityStackObserver.getCurrentActivity();
    }

    public boolean isInForeground() {
        return mActivityStackObserver.isInForeground();
    }

    private void monitorConnectivityChange() {
        FragmentActivity activity = (FragmentActivity) currentActivity();

        mConnectivityObservable = new ConnectivityLiveData(this);
        mConnectivityObservable.observeForever(mConnectivityObserver = new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean aBoolean) {}
        });
    }

    public ConnectivityLiveData getConnectivityObservable() {
        return mConnectivityObservable;
    }

    public void exitApplication() {
        releaseResources();
        forceKillProcess();
    }

    protected void releaseResources() {
        if (mConnectivityObservable != null && mConnectivityObserver != null) {
            mConnectivityObservable.removeObserver(mConnectivityObserver);
        }
    }

    protected void forceKillProcess() {
        Process.killProcess(Process.myPid());
    }
}
