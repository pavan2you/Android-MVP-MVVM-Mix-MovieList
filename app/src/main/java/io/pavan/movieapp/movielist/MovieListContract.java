package io.pavan.movieapp.movielist;

import android.arch.lifecycle.LifecycleOwner;

import java.util.List;

import io.pavan.movieapp.arch.mvp.IPresenter;
import io.pavan.movieapp.arch.mvp.IView;
import io.pavan.movieapp.data.model.Movie;

/**
 * Created by pavan on 01/12/18
 */
public interface MovieListContract {

    interface IMovieListView extends IView {

        void showDataLoading();

        void hideDataLoading();

        void setDataModel(List<Movie> modelList);

        void setNoDataAvailableMessage(String noDataMessage);

        void showNoDataMessage();

        void hideNoDataMessage();

        void showDataList();

        void hideDataList();

        MovieListViewModel getMovieListViewModel();

        LifecycleOwner getLifeCycleOwner();

        void showErrorMessage(String message, boolean withRetryOption);

        void showMovieView(Movie movie);

        void exitApplication();
    }

    interface IMovieListPresenter extends IPresenter<IMovieListView> {

        void onErrorRetryClick();

        void onMovieListItemClick(Movie model);
    }
}
