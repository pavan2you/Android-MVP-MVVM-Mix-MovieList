package io.pavan.movieapp.arch;

import io.pavan.movieapp.arch.data.IDaoFactory;
import io.pavan.movieapp.arch.data.net.IApiFactory;

/**
 * Created by pavan on 02/12/18
 */
public class Injector {

    private static Injector sInstance = new Injector();

    public static Injector instance() {
        return sInstance;
    }

    private LifeCycleAwareApplication mApplication;
    private IDaoFactory mDaoFactory;
    private IApiFactory mApiFactory;

    private Injector() {
    }

    public void setApplication(LifeCycleAwareApplication application) {
        mApplication = application;
    }

    public LifeCycleAwareApplication getApplication() {
        return mApplication;
    }

    public void setApiFactory(IApiFactory apiFactory) {
        mApiFactory = apiFactory;
    }

    public IApiFactory getApiFactory() {
        return mApiFactory;
    }

    public void setDaoFactory(IDaoFactory daoFactory) {
        mDaoFactory = daoFactory;
    }

    public IDaoFactory getDaoFactory() {
        return mDaoFactory;
    }
}
