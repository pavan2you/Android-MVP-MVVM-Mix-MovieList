package io.pavan.movieapp.arch.mvp;

/**
 * Created by pavan on 01/12/18
 *
 * A life cycle aware presenter.
 */
public interface IPresenter<V extends IView> {

    V view();

    void setView(V view);

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onBackPressed();

    void onDestroy();
}
