package io.pavan.movieapp.data.repository;

/**
 * Created by pavan on 02/12/18
 */
public class MovieDaoFactory implements IMovieDaoFactory {

    private MovieRepository mMovieRepository;

    @Override
    public MovieRepository movie() {
        if (mMovieRepository == null) {
            mMovieRepository = new MovieRepository();
        }
        return mMovieRepository;
    }

    @Override
    public void release() {
    }
}
