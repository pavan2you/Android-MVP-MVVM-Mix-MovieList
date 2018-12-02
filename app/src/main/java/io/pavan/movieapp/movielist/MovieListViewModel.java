package io.pavan.movieapp.movielist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import io.pavan.movieapp.arch.Injector;
import io.pavan.movieapp.arch.data.net.ApiCallback;
import io.pavan.movieapp.arch.data.net.ApiException;
import io.pavan.movieapp.data.model.Movie;
import io.pavan.movieapp.data.repository.MovieDaoFactory;
import io.pavan.movieapp.data.repository.MovieRepository;

/**
 * Created by pavan on 02/12/18
 */
public class MovieListViewModel extends AndroidViewModel {

    private MovieRepository mRepository;

    private MutableLiveData<Boolean> mLoadingWatcher;
    private MutableLiveData<List<Movie>> mObservableMovies;
    private MutableLiveData<Throwable> mObservableError;

    public MovieListViewModel(@NonNull Application application) {
        super(application);

        mRepository = ((MovieDaoFactory) Injector.instance().getDaoFactory()).movie();

        mLoadingWatcher = new MutableLiveData<>();
        mObservableMovies = new MutableLiveData<>();
        mObservableError = new MutableLiveData<>();

        mLoadingWatcher.setValue(false);
        mObservableError.setValue(null);
        mObservableMovies.setValue(null);
    }

    public MutableLiveData<Boolean> getLoadingWatcher() {
        return mLoadingWatcher;
    }

    public MutableLiveData<Throwable> getObservableError() {
        return mObservableError;
    }

    public MutableLiveData<List<Movie>> getMoviesObservable() {
        return mObservableMovies;
    }

    public MutableLiveData<List<Movie>> getMovies() {

        if (isLoading()) {
            return mObservableMovies;
        }

        mLoadingWatcher.setValue(true);
        mObservableError.setValue(null);

        Boolean connected = Injector.instance().getApplication().getConnectivityObservable()
                .getValue();

        mRepository.getMovies(true, connected, new ApiCallback<List<Movie>>() {

            @Override
            public void onResult(List<Movie> result) {
                mObservableError.setValue(null);
                mObservableMovies.setValue(result);
                mLoadingWatcher.setValue(false);
            }

            @Override
            public void onError(ApiException error) {
                mObservableError.setValue(error);
                mObservableMovies.setValue(null);
                mLoadingWatcher.setValue(false);
            }
        });

        return mObservableMovies;
    }

    private boolean isLoading() {
        Boolean value = mLoadingWatcher.getValue();
        return value != null && value;
    }
}
