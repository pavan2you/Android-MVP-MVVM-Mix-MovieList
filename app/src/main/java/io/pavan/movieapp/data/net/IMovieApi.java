package io.pavan.movieapp.data.net;

import io.pavan.movieapp.arch.data.net.Api;
import io.pavan.movieapp.arch.data.net.ApiCallback;
import io.pavan.movieapp.data.model.MovieList;

/**
 * Created by pavan on 02/12/18
 */
public interface IMovieApi extends Api<MovieList> {

    void getMovieList(ApiCallback<MovieList> callback);
}
