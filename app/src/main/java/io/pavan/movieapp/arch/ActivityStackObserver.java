package io.pavan.movieapp.arch;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by pavan on 02/12/18
 */
class ActivityStackObserver implements Application.ActivityLifecycleCallbacks {

    private WeakReference<Activity> mCurrentActivityWeakRef;
    private WeakReference<Activity> mPreviousActivityWeakRef;

    private boolean mIsInForeground;

    ActivityStackObserver() {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        setCurrentActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mIsInForeground = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mIsInForeground = true;
        setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        if (mCurrentActivityWeakRef == null || mCurrentActivityWeakRef.get() == null) {
            mIsInForeground = false;
            return;
        }

        Activity current = mCurrentActivityWeakRef.get();
        if (current == activity) {
            mIsInForeground = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (mCurrentActivityWeakRef != null) {
            Activity current = mCurrentActivityWeakRef.get();
            if (current == activity) {
                mPreviousActivityWeakRef = mCurrentActivityWeakRef;
                mCurrentActivityWeakRef = null;
            }
        }

        if (mPreviousActivityWeakRef != null) {
            Activity previous = mPreviousActivityWeakRef.get();
            if (previous == activity) {
                mPreviousActivityWeakRef = null;
            }
        }
    }

    private void setCurrentActivity(Activity activity) {
        if (mCurrentActivityWeakRef != null) {
            Activity current = mCurrentActivityWeakRef.get();
            if (mPreviousActivityWeakRef != null) {
                Activity previous = mPreviousActivityWeakRef.get();
                if (previous != current) {
                    mPreviousActivityWeakRef = mCurrentActivityWeakRef;
                }
            }
            else {
                mPreviousActivityWeakRef = mCurrentActivityWeakRef;
            }
        }

        mCurrentActivityWeakRef = new WeakReference<>(activity);
    }

    public Activity getCurrentActivity() {
        return mCurrentActivityWeakRef == null || mCurrentActivityWeakRef.get() == null ?
                null : mCurrentActivityWeakRef.get();
    }

    public Activity getPreviousActivity() {
        return mPreviousActivityWeakRef == null || mPreviousActivityWeakRef.get() == null ?
                null : mPreviousActivityWeakRef.get();
    }

    public boolean isInForeground() {
        return mIsInForeground;
    }
}
