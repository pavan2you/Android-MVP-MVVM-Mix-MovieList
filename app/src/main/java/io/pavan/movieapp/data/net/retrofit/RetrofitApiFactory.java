package io.pavan.movieapp.data.net.retrofit;

import io.pavan.movieapp.util.Config;
import io.pavan.movieapp.data.net.IMovieApi;
import io.pavan.movieapp.data.net.IMovieApiFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pavan on 02/12/18
 */
public class RetrofitApiFactory implements IMovieApiFactory {

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .baseUrl(Config.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    @Override
    public IMovieApi movies() {
        return new MovieApi(builder(MovieApiProxy.class));
    }

    @Override
    public void release() {
    }
}
