package io.pavan.movieapp.data.repository;

import io.pavan.movieapp.arch.data.IDaoFactory;

/**
 * Created by pavan on 02/12/18
 */
public interface IMovieDaoFactory extends IDaoFactory {

    MovieRepository movie();
}
