package io.pavan.movieapp.data.model;

import java.util.List;
import java.util.Objects;

/**
 * Created by pavan on 02/12/18
 */
public class MovieList {

    List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovieList movieList = (MovieList) o;
        return Objects.equals(movies, movieList.movies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movies);
    }
}
