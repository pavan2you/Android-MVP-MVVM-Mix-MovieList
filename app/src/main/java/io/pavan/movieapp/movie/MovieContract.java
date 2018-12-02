package io.pavan.movieapp.movie;

import android.support.v4.util.Pair;

import java.util.List;

import io.pavan.movieapp.arch.mvp.IPresenter;
import io.pavan.movieapp.arch.mvp.IView;
import io.pavan.movieapp.data.model.Movie;

/**
 * Created by pavan on 01/12/18
 */
public interface MovieContract {

    interface IMovieView extends IView {

        void setTitleWith(String title);

        void setPosterImage(int resId);

        void setPosterImage(String posterUrl);

        void setDataModel(List<Pair<String,String>> dataModel);

        void showCallerView();
    }

    interface IMoviePresenter extends IPresenter<IMovieView> {

        void loadWith(Movie movie);
    }

}
