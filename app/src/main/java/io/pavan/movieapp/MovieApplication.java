package io.pavan.movieapp;

import io.pavan.movieapp.arch.Injector;
import io.pavan.movieapp.arch.LifeCycleAwareApplication;

/**
 * Created by pavan on 02/12/18
 */
public class MovieApplication extends LifeCycleAwareApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Injection.inject();
    }

    @Override
    protected void releaseResources() {
        super.releaseResources();

        Injector injector = Injector.instance();
        injector.getDaoFactory().release();
        injector.getApiFactory().release();
    }
}
