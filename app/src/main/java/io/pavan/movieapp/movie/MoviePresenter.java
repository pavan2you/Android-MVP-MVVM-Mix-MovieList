package io.pavan.movieapp.movie;

import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.pavan.movieapp.R;
import io.pavan.movieapp.arch.mvp.Presenter;
import io.pavan.movieapp.data.model.Movie;

/**
 * Created by pavan on 02/12/18
 */
public class MoviePresenter extends Presenter<MovieContract.IMovieView>
        implements MovieContract.IMoviePresenter {

    private Movie mMovie;
    private List<Pair<String, String>> mMoviePropertyList;

    public MoviePresenter(MovieContract.IMovieView view) {
        super(view);
    }

    public void loadWith(Movie movie) {
        mMovie = movie;
        /*
         * The other possibilities are
         *
         * Just get movieId (but the example movies json doesn't have, lets say its there then
         *
         * Ask ViewModel to return movie object based on movieId.
         *
         * Scenario 1 : if its there it will return
         * Scenario 2 : Assuming MovieActivity is launched from recents but data is not available
         * then fetch entire movie list and locate this item and return only that.
         */

        mMoviePropertyList = prepareMoviePropertyList(mMovie);
    }

    @Override
    public void onStart() {
        super.onStart();

        bindMovie(mMovie, mMoviePropertyList);
    }

    @VisibleForTesting
    public List<Pair<String, String>> prepareMoviePropertyList(Movie movie) {
        List<Pair<String, String>> list = new ArrayList<>();

        list.add(newPair(R.string.movie_info_title, movie.getTitle()));
        list.add(newPair(R.string.movie_info_year, movie.getYear()));
        list.add(newPair(R.string.movie_info_rated, movie.getRated()));
        list.add(newPair(R.string.movie_info_released, movie.getReleased()));
        list.add(newPair(R.string.movie_info_runtime, movie.getRuntime()));
        list.add(newPair(R.string.movie_info_genre, movie.getGenre()));
        list.add(newPair(R.string.movie_info_director, movie.getDirector()));
        list.add(newPair(R.string.movie_info_writer, movie.getWriter()));
        list.add(newPair(R.string.movie_info_actors, movie.getActors()));
        list.add(newPair(R.string.movie_info_plot, movie.getPlot()));
        list.add(newPair(R.string.movie_info_language, movie.getLanguage()));
        list.add(newPair(R.string.movie_info_country, movie.getCountry()));
        list.add(newPair(R.string.movie_info_awards, movie.getAwards()));

        return list;
    }

    private Pair<String, String> newPair(int resId, String value) {
        return new Pair<>(getString(resId), value);
    }

    @VisibleForTesting
    public void bindMovie(Movie movie, List<Pair<String, String>> propertyList) {

        String title = getString(R.string.movie_info);
        if (isValidTitle(movie.getTitle())) {
            title += " - " + movie.getTitle();
        }
        view().setTitleWith(title);

        String poster = movie.getPoster();
        if (isValidPosterUrl(poster)) {
            view().setPosterImage(poster);
        }
        else {
            view().setPosterImage(android.R.drawable.gallery_thumb);
        }

        view().setDataModel(propertyList);
    }

    private boolean isValidTitle(String title) {
        return title != null && !title.trim().equalsIgnoreCase("N/A");
    }

    private boolean isValidPosterUrl(String poster) {
        return poster != null && (poster.startsWith("http"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (view() != null) {
            view().showCallerView();
        }
    }
}
