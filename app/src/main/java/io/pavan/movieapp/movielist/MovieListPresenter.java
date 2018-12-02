package io.pavan.movieapp.movielist;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.List;

import io.pavan.movieapp.R;
import io.pavan.movieapp.arch.data.net.ApiException;
import io.pavan.movieapp.arch.mvp.Presenter;
import io.pavan.movieapp.data.model.Movie;

/**
 * Created by pavan on 02/12/18
 */
public class MovieListPresenter extends Presenter<MovieListContract.IMovieListView>
        implements MovieListContract.IMovieListPresenter {

    private MutableLiveData<Boolean> mLoadingWatcher;
    private MutableLiveData<Throwable> mErrorObservable;
    private MutableLiveData<List<Movie>> mMoviesObservable;

    public MovieListPresenter(MovieListContract.IMovieListView view) {
        super(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        fetchDataModel();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  DATA LIFE CYCLE  ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void fetchDataModel() {
        MovieListViewModel movieListViewModel = view().getMovieListViewModel();
        LifecycleOwner lifecycleOwner = view().getLifeCycleOwner();
        fetchDataModel(movieListViewModel, lifecycleOwner);
    }

    private void fetchDataModel(MovieListViewModel movieListViewModel,
            LifecycleOwner lifecycleOwner) {

        checkIsValidFetchCall(movieListViewModel, lifecycleOwner);

        subscribeObserversIfNeeded(lifecycleOwner, movieListViewModel);
        movieListViewModel.getMovies();
    }

    private void checkIsValidFetchCall(MovieListViewModel mlvm, LifecycleOwner lo) {
        if (mlvm == null || lo == null) {
            throw new IllegalArgumentException("View should provide MovieListViewModel and " +
                    "LifecycleOwner");
        }
    }

    private void subscribeObserversIfNeeded(LifecycleOwner lifecycleOwner,
            MovieListViewModel movieListViewModel) {

        if (mLoadingWatcher != null) {
            return;
        }

        mLoadingWatcher = movieListViewModel.getLoadingWatcher();
        mErrorObservable = movieListViewModel.getObservableError();
        mMoviesObservable = movieListViewModel.getMoviesObservable();

        mLoadingWatcher.observe(lifecycleOwner, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean show) {
                onListProgressChanged(show != null && show);
            }
        });

        mErrorObservable.observe(lifecycleOwner, new Observer<Throwable>() {

            @Override
            public void onChanged(@Nullable Throwable throwable) {
                if (throwable == null) {
                    return;
                }
                onListLoadingFailed(throwable);
            }
        });

        mMoviesObservable.observe(lifecycleOwner, new Observer<List<Movie>>() {

            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                onListLoadingCompleted(movies, mLoadingWatcher.getValue(),
                        mErrorObservable.getValue());
            }
        });
    }

    private void onListProgressChanged(boolean show) {
        if (show) {
            view().showDataLoading();
            view().hideDataList();

            view().setNoDataAvailableMessage(getString(R.string.movie_list_is_loading));
            view().showNoDataMessage();
        }
        else {
            view().hideDataLoading();
        }
    }

    private void onListLoadingFailed(Throwable throwable) {
        if (throwable instanceof ApiException) {
            onMovieApiException((ApiException) throwable);
        }
        else {
            onSomeOtherException(throwable);
        }
    }

    @VisibleForTesting
    public void onMovieApiException(ApiException e) {
        switch (e.severity) {
        case NO_NETWORK:
            onNoNetworkException();
            break;

        default:
            onSomeOtherException(e);
            break;
        }
    }

    private void onNoNetworkException() {
        view().showErrorMessage(getString(R.string.general_no_network_error),
                true);
    }

    private void onSomeOtherException(Throwable throwable) {
        view().showErrorMessage(getString(R.string.general_unrecoverable_error),
                true);
    }

    @VisibleForTesting
    public void onListLoadingCompleted(List<Movie> movies, Boolean loading, Throwable error) {
        if (movies == null || movies.size() == 0) {

            String message = loading != null && loading ?
                    getString(R.string.movie_list_is_loading) :
                    getString(R.string.movie_list_not_available);

            if (error != null) {
                message = getString(R.string.general_any_failure);
            }

            view().setNoDataAvailableMessage(message);
            view().hideDataList();
            view().showNoDataMessage();
        }
        else {
            view().showDataList();
            view().hideNoDataMessage();
            view().setDataModel(movies);
        }
    }

    private void removeDataObservers() {
        MovieListViewModel movieListViewModel = view().getMovieListViewModel();
        LifecycleOwner lifecycleOwner = view().getLifeCycleOwner();
        checkIsValidFetchCall(movieListViewModel, lifecycleOwner);

        movieListViewModel.getLoadingWatcher().removeObservers(lifecycleOwner);
        movieListViewModel.getObservableError().removeObservers(lifecycleOwner);
        movieListViewModel.getMovies().removeObservers(lifecycleOwner);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  VIEW INTERACTIONS  //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onErrorRetryClick() {
        fetchDataModel();
    }

    @Override
    public void onMovieListItemClick(Movie model) {
        view().showMovieView(model);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////  VIEW MOVING OUT  ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (view() != null) {
            view().exitApplication();
        }
    }

    @Override
    public void onDestroy() {
        /*
         * When subclasses are doing heavy operation, early setting this flag helps to prevent
         * crashes due to async programming
         */
        setState(State.ON_DESTROY_EXTERNAL);

        mLoadingWatcher = null;
        mErrorObservable = null;
        mMoviesObservable = null;
        removeDataObservers();

        super.onDestroy();
    }
}
