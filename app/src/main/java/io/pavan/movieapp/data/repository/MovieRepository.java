package io.pavan.movieapp.data.repository;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import io.pavan.movieapp.arch.Injector;
import io.pavan.movieapp.arch.data.net.ApiCallback;
import io.pavan.movieapp.arch.data.net.ApiException;
import io.pavan.movieapp.data.model.Movie;
import io.pavan.movieapp.data.model.MovieList;
import io.pavan.movieapp.data.net.IMovieApi;
import io.pavan.movieapp.data.net.IMovieApiFactory;

/**
 * Created by pavan on 02/12/18
 */
public class MovieRepository {

    private IMovieApi mWebservice;
    private MutableLiveData<MovieList> mMovieObservable;

    public MovieRepository() {
        mWebservice = ((IMovieApiFactory) Injector.instance().getApiFactory()).movies();
        mMovieObservable = new MutableLiveData<>();
    }

    public void getMovies(boolean checkInCacheFirst, Boolean connected,
            final ApiCallback<List<Movie>> callback) {

        MovieListApiCallback internalCallback = new MovieListApiCallback(callback);

        /*
         * Simplest cache implementation. Use LiveData as cache also
         */
        if (checkInCacheFirst && mMovieObservable.getValue() != null) {
            internalCallback.onResult(mMovieObservable.getValue());
        }

        if (connected == null || connected) {
            mWebservice.getMovieList(internalCallback);
        }
        else {
            ApiException exception = new ApiException(ApiException.Severity.NO_NETWORK,
                    "Network not reachable");
            internalCallback.onError(exception);
        }
    }

    private class MovieListApiCallback implements ApiCallback<MovieList> {

        private ApiCallback<List<Movie>> mForwardCallback;

        MovieListApiCallback(ApiCallback<List<Movie>> forward) {
            mForwardCallback = forward;
        }

        @Override
        public void onResult(MovieList result) {
            if (result != null) {
                mForwardCallback.onResult(result.getMovies());
            }
            else {
                mForwardCallback.onResult(null);
            }
        }

        @Override
        public void onError(ApiException error) {
            mForwardCallback.onError(error);
        }
    }
}
