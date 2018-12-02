package io.pavan.movieapp.data.net;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.pavan.movieapp.Injection;
import io.pavan.movieapp.MovieApplication;
import io.pavan.movieapp.arch.Injector;
import io.pavan.movieapp.arch.data.net.ApiCallback;
import io.pavan.movieapp.arch.data.net.ApiException;
import io.pavan.movieapp.data.model.Movie;
import io.pavan.movieapp.data.model.MovieList;

import static org.junit.Assert.assertNotNull;

/**
 * Created by pavan on 02/12/18
 */
public class MovieApiTest {

    @Mock
    private MovieApplication mApplication;

    private IMovieApi mMovieApi;

    @Mock
    private ApiCallback<List<Movie>> mMovieListApiCallback;

    @Before
    public void setupMovieApi() {
        MockitoAnnotations.initMocks(this);
        Injection.inject();
        Injector.instance().setApplication(mApplication);

        mMovieApi = ((IMovieApiFactory) Injector.instance().getApiFactory()).movies();
    }

    @Test
    public void getMovieList_requestAllMoviesFromMoviesApi() {
        mMovieApi.getMovieList(new ApiCallback<MovieList>() {
            @Override
            public void onResult(MovieList result) {
                assertNotNull(result);
            }

            @Override
            public void onError(ApiException error) {
                assertNotNull(error);
            }
        });
    }
}
