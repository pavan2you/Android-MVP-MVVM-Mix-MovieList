package io.pavan.movieapp.arch.mvp;

import android.app.Application;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;

import io.pavan.movieapp.arch.Injector;

/**
 * Created by pavan on 02/12/18
 *
 * This is the simplest life cycle aware presenter implementation. It can extend to handle
 * Parent Presenter and/or fragment/child presenters. In the context of this exercise just limited
 * to simple presenter implementation.
 *
 * For more complete implementation refer
 * https://github.com/pavan2you/jVanilaLite/blob/master/jvanila/src/main/java/com/jvanila/mobile/mvp/Presenter.java
 *
 */
public class Presenter<V extends IView> implements IPresenter<V> {

    protected enum State {
        ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY_EXTERNAL, ON_DESTROY
    }

    protected AtomicReference<State> mCurrentState;
    protected WeakReference<V> mViewWeakReference;

    public Presenter(V view) {
        mCurrentState = new AtomicReference<>();
        setView(view);
    }

    public V view() {
        return mViewWeakReference == null ? null : mViewWeakReference.get();
    }

    public void setView(V view) {
        mViewWeakReference = new WeakReference<>(view);
    }

    public void onCreate() {
        setState(State.ON_CREATE);
    }

    public void onStart() {
        setState(State.ON_START);
    }

    public void onResume() {
        setState(State.ON_RESUME);
    }

    public void onPause() {
        setState(State.ON_PAUSE);
    }

    public void onStop() {
        setState(State.ON_STOP);
    }

    public void onBackPressed() {
    }

    public void onDestroy() {
        if (mCurrentState.get() == State.ON_DESTROY) {
            return;
        }

        if (mCurrentState.get() != State.ON_STOP) {
            onStop();
        }

        setState(State.ON_DESTROY);

        mViewWeakReference.clear();
        mViewWeakReference = null;
    }

    protected void setState(State state) {
        mCurrentState.set(state);
    }

    protected boolean isDestroying() {
        State state = mCurrentState.get();
        return state == State.ON_DESTROY_EXTERNAL || state == State.ON_DESTROY;
    }

    protected String getString(int resId) {
        Application application = Injector.instance().getApplication();
        return application.getResources() != null ?
                application.getResources().getString(resId) : "";
    }
}
