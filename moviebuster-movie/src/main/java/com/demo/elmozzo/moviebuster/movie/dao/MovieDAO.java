package com.demo.elmozzo.moviebuster.movie.dao;

import static java.util.Objects.requireNonNull;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * The Class MovieDAO.
 */
public class MovieDAO extends AbstractDAO<Movie> {

	/**
	 * Instantiates a new movie DAO.
	 *
	 * @param sessionFactory
	 *          the session factory
	 */
	public MovieDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * Persist the new movie on db
	 *
	 * @param movie
	 *          the movie
	 * @return the movie
	 */
	public Movie create(Movie movie) {
		final Date now = MovieBusterUtils.now();
		movie.setInsertDate(now);
		movie.setUpdateDate(now);
		return this.persist(requireNonNull(movie));
	}

	/**
	 * Delete a movie by id
	 *
	 * @param movieId
	 *          the movie id
	 * @return the movie
	 */
	public Movie delete(Long movieId) {
		final Optional<Movie> dbMovie = this.findById(movieId);
		if (dbMovie.isPresent()) {
			this.delete(dbMovie.get());
			return dbMovie.get();
		} else {
			return null;
		}
	}

	/**
	 * Delete a movie
	 *
	 * @param movie
	 *          the movie
	 */
	public Movie delete(Movie movie) {
		this.currentSession().delete(requireNonNull(movie));
		return movie;
	}

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	public List<Movie> findAll() {
		return this.list(this.namedQuery("com.demo.elmozzo.moviebuster.object.Movie.findAll"));
	}

	/**
	 * Find by id.
	 *
	 * @param id
	 *          the id
	 * @return the optional
	 */
	public Optional<Movie> findById(Long id) {
		return Optional.ofNullable(this.get(id));
	}

	/**
	 * Update.
	 *
	 * @param movie
	 *          the movie
	 * @return the movie
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Movie update(Movie movie, Long movieId) {
		final Date now = MovieBusterUtils.now();
		final Optional<Movie> dbMovieOptional = this.findById(movieId);
		if (dbMovieOptional.isPresent()) {
			movie.setUpdateDate(now);
			final Movie dbMovie = (Movie) dbMovieOptional.get().mergeFrom(movie);
			return this.persist(dbMovie);
		} else {
			return null;
		}
	}

}
