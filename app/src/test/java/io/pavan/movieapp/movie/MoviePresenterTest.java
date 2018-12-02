package io.pavan.movieapp.movie;

import android.support.v4.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.pavan.movieapp.Injection;
import io.pavan.movieapp.MovieApplication;
import io.pavan.movieapp.arch.Injector;
import io.pavan.movieapp.data.model.Movie;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by pavan on 02/12/18
 */
public class MoviePresenterTest {

    @Mock
    private MovieContract.IMovieView mView;

    @Mock
    private Movie mMovie;

    @Mock
    private List<Pair<String, String>> mMoviePropertyList;

    @Mock
    private MovieApplication mApplication;

    private MoviePresenter mPresenter;

    @Before
    public void setupMoviePresenter() {
        MockitoAnnotations.initMocks(this);
        Injection.inject();
        Injector.instance().setApplication(mApplication);

        mPresenter = new MoviePresenter(mView);
    }

    @Test
    public void clickOnBackPressed_ShowCallerView() {
        Movie requestedMovie = new Movie();
        mPresenter.onBackPressed();
        verify(mView).showCallerView();
    }

    @Test
    public void bindView() {
        mPresenter.bindMovie(mMovie, mMoviePropertyList);

        verify(mView).setTitleWith(any(String.class));
        verify(mView).setPosterImage(any(Integer.class));
        verify(mView).setDataModel(mMoviePropertyList);
    }
}
