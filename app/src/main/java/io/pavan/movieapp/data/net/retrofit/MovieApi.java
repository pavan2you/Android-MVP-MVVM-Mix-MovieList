package io.pavan.movieapp.data.net.retrofit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.pavan.movieapp.arch.data.net.ApiCallback;
import io.pavan.movieapp.arch.data.net.ApiException;
import io.pavan.movieapp.data.model.MovieList;
import io.pavan.movieapp.data.net.IMovieApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pavan on 02/12/18
 */
public class MovieApi implements IMovieApi {

    private MovieApiProxy mProxy;

    public MovieApi(MovieApiProxy proxy) {
        mProxy = proxy;
    }

    @Override
    public void getMovieList(final ApiCallback<MovieList> callback) {
        mProxy.getMovieList().enqueue(new GetMovieListCallback(callback));
    }

    private static class GetMovieListCallback implements Callback<MovieList> {

        private ApiCallback<MovieList> mRedirectCallback;

        GetMovieListCallback(ApiCallback<MovieList> callback) {
            mRedirectCallback = callback;
        }

        @Override
        public void onResponse(@NonNull Call<MovieList> call,
                @NonNull Response<MovieList> response) {

            if (response.isSuccessful()) {

                Log.w("MovieList => ",new Gson().toJson(response));

                mRedirectCallback.onResult(response.body());
            }
            else if (response.errorBody() != null) {
                try {
                    String error = response.errorBody().string();
                    mRedirectCallback.onError(new ApiException(ApiException.Severity.CONTENT,
                            error));
                }
                catch (Exception e) {

                    ApiException.Severity severity = ApiException.Severity.CONTENT;
                    onApiException(e, severity);
                }
            }
            else {
                mRedirectCallback.onError(new ApiException(ApiException.Severity.CONTENT_UNDEFINED,
                        "Unresolved response"));
            }
        }

        private void onApiException(Throwable t, ApiException.Severity severity) {
            if (t instanceof UnknownHostException || t instanceof SocketTimeoutException) {
                severity = ApiException.Severity.NO_NETWORK;
            }
            ApiException ae = new ApiException(severity, "");
            ae.initCause(t);

            mRedirectCallback.onError(ae);
        }

        @Override
        public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {
            onApiException(t, ApiException.Severity.CRITICAL);
        }
    }
}
