package io.pavan.movieapp.arch.data.net;

/**
 * Created by pavan on 02/12/18
 */
public interface ApiCallback<T> {

    void onResult(T result);

    void onError(ApiException error);
}
