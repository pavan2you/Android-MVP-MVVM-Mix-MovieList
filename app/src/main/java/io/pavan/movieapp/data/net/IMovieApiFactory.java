package io.pavan.movieapp.data.net;

import io.pavan.movieapp.arch.data.net.IApiFactory;

/**
 * Created by pavan on 02/12/18
 */
public interface IMovieApiFactory extends IApiFactory {

    IMovieApi movies();
}
