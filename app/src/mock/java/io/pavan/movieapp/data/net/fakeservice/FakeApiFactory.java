package io.pavan.movieapp.data.net.fakeservice;

import io.pavan.movieapp.data.net.IMovieApi;
import io.pavan.movieapp.data.net.IMovieApiFactory;

/**
 * Created by pavan on 02/12/18
 */
public class FakeApiFactory implements IMovieApiFactory {

    @Override
    public IMovieApi movies() {
        return new FakeMovieApi();
    }

    @Override
    public void release() {

    }
}
