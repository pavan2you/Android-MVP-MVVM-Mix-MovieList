package io.pavan.movieapp;

import io.pavan.movieapp.arch.Injector;
import io.pavan.movieapp.data.net.fakeservice.FakeApiFactory;
import io.pavan.movieapp.data.repository.MovieDaoFactory;

/**
 * Created by pavan on 02/12/18
 */
public class Injection {

    public static void inject() {
        Injector injector = Injector.instance();
        injector.setDaoFactory(new MovieDaoFactory());
        injector.setApiFactory(new FakeApiFactory());
    }
}
