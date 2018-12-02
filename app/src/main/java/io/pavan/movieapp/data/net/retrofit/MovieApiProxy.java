package io.pavan.movieapp.data.net.retrofit;

import io.pavan.movieapp.data.model.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pavan on 02/12/18
 */
public interface MovieApiProxy {

    @GET("bins/18buhu")
    Call<MovieList> getMovieList();
}
