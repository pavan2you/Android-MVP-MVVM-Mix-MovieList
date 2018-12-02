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
import io.pavan.movieapp.data.repository.IMovieDaoFactory;
import io.pavan.movieapp.data.repository.MovieRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by pavan on 02/12/18
 */
public class MovieRepositoryTest {

    @Mock
    private MovieApplication mApplication;

    private MovieRepository mMovieRepository;

    @Mock
    private ApiCallback<List<Movie>> mMovieListApiCallback;

    @Before
    public void setupMovieRepository() {
        MockitoAnnotations.initMocks(this);
        Injection.inject();
        Injector.instance().setApplication(mApplication);

        mMovieRepository = ((IMovieDaoFactory) Injector.instance().getDaoFactory()).movie();
    }

    @Test
    public void getMovieList_whenConnectedToNetwork() {
        mMovieRepository.getMovies(true, true,
                new ApiCallback<List<Movie>>() {

            @Override
            public void onResult(List<Movie> result) {
                assertNotNull(result);
            }

            @Override
            public void onError(ApiException error) {
                assertNotNull(error);
            }
        });
    }

    @Test
    public void getMovieList_whenNotConnectedToNetwork() {
        mMovieRepository.getMovies(true, false,
                new ApiCallback<List<Movie>>() {

                    @Override
                    public void onResult(List<Movie> result) {
                        assertNull(result);
                    }

                    @Override
                    public void onError(ApiException error) {
                        assertNotNull(error);
                    }
                });
    }

    @Test
    public void getMovieList_FetchOnlyFromNetwork() {
        mMovieRepository.getMovies(false, true,
                new ApiCallback<List<Movie>>() {

                    @Override
                    public void onResult(List<Movie> result) {
                        assertNotNull(result);
                    }

                    @Override
                    public void onError(ApiException error) {
                        assertNotNull(error);
                    }
                });
    }

    @Test
    public void getMovieList_FetchOnlyFromCache() {
        mMovieRepository.getMovies(true, true,
                new ApiCallback<List<Movie>>() {

                    @Override
                    public void onResult(List<Movie> result) {
                        assertNotNull(result);
                    }

                    @Override
                    public void onError(ApiException error) {
                        assertNotNull(error);
                    }
                });
    }

    @Test
    public void getMovieList_FetchOnlyFromCacheAndWhenNetworkOff() {
        mMovieRepository.getMovies(true, false,
                new ApiCallback<List<Movie>>() {

                    @Override
                    public void onResult(List<Movie> result) {
                        assertNotNull(result);
                    }

                    @Override
                    public void onError(ApiException error) {
                        assertNotNull(error);
                    }
                });
    }
}
