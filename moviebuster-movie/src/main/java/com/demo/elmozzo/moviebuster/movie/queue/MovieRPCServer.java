package com.demo.elmozzo.moviebuster.movie.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.cfg.NotYetImplementedException;

import com.demo.elmozzo.moviebuster.movie.dao.MovieDAO;
import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;
import com.demo.elmozzo.queue.client.RPCServer;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.rabbitmq.client.Channel;

import io.dropwizard.hibernate.UnitOfWork;

/**
 * The Class MovieRPCServer. Sticks togheter db-hibernate and rabbit queue
 */
public class MovieRPCServer extends RPCServer {

	/** The Constant CREATE_ACTION. */
	public static final String CREATE_ACTION = "create";

	/** The Constant UPDATE_ACTION. */
	public static final String UPDATE_ACTION = "update";

	/** The Constant DELETE_ACTION. */
	public static final String DELETE_ACTION = "delete";

	/** The Constant GET_ACTION. */
	public static final String GET_ACTION = "get";

	/**
	 * Gets the constructor arguments stacked for reflection construction
	 * invokation
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param bonusDAO
	 *          the bonus DAO
	 * @return the constructor arguments
	 */
	public static Object[] getConstructorArguments(Channel channel, String queueName, MovieDAO movieDAO) {
		final Object[] out = { channel, queueName, movieDAO };
		return out;
	}

	/**
	 * Gets the constructor param types. Useful for unit of work init
	 *
	 * @return the constructor param types
	 */
	public static Class<?>[] getConstructorParamTypes() {
		final Class<?>[] out = { Channel.class, String.class, MovieDAO.class };
		return out;
	}

	/** The movie DAO. */
	private MovieDAO movieDAO;

	/**
	 * Instantiates a new movie RPC server.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param movieDAO
	 *          the movie DAO
	 */
	public MovieRPCServer(Channel channel, String queueName, MovieDAO movieDAO) {
		super(channel, queueName);
		this.setMovieDAO(movieDAO);
	}

	/**
	 * Creates the movie
	 *
	 * @param req
	 *          the request
	 * @param res
	 *          the response
	 */
	public void create(final RPCMessage req, final RPCMessage res) {
		// parse Movie object
		final Movie movie = Movie.fromString(req.getObj());
		if (movie != null) {

			// create the object on db
			final Movie createdMovie = this.getMovieDAO().create(movie);
			// return the created object
			res.setObj(createdMovie.toString());
		} else {
			res.setObj(null);
		}
	}

	/**
	 * Gets the movie DAO.
	 *
	 * @return the movie DAO
	 */
	public MovieDAO getMovieDAO() {
		return this.movieDAO;
	}

	/**
	 * Sets the movie DAO.
	 *
	 * @param movieDAO
	 *          the new movie DAO
	 */
	public void setMovieDAO(MovieDAO movieDAO) {
		this.movieDAO = movieDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.queue.client.RabbitMQRPCServer#getValue(java.lang.
	 * String)
	 */
	@SuppressWarnings("finally")
	@Override
	@UnitOfWork
	protected String getValue(String message) throws Exception {
		// CORE FUNCTIONALITY OF SERVER

		final RPCMessage res = new RPCMessage("result", null);
		try {

			// parse received message
			final RPCMessage req = RPCMessage.fromString(message);

			// action router
			if (req.getAction() != null && req.getAction().equals(CREATE_ACTION)) {
				this.create(req, res);
			} else if (req.getAction() != null && req.getAction().equals(UPDATE_ACTION)) {
				this.update(req, res);
			} else if (req.getAction() != null && req.getAction().equals(DELETE_ACTION)) {
				this.delete(req, res);
			} else if (req.getAction() != null && req.getAction().equals(GET_ACTION)) {
				this.get(req, res);
			} else {
				// action not allowed on this ms
				throw new NotYetImplementedException();
			}

		} catch (final Exception ex) {
			this.log.error("Get-Value error", ex);
			res.setException(ex);
		} finally {
			return res.toString();
		}
	}

	/**
	 * Delete.
	 *
	 * @param req
	 *          the req
	 * @param res
	 *          the res
	 */
	private void delete(RPCMessage req, RPCMessage res) {

		// parse update params
		final Object id = req.getParams().get("id");

		if (id != null && (id instanceof Long || id instanceof Integer)) {

			// get id
			final Long movieId = Long.parseLong(id.toString());
			// create the object on db
			final Movie deletedMovie = this.getMovieDAO().delete(movieId);
			// return the created object
			res.setObj(deletedMovie != null ? deletedMovie.toString() : null);

		} else {

			res.setObj(null);
		}

	}

	/**
	 * Gets the.
	 *
	 * @param req
	 *          the req
	 * @param res
	 *          the res
	 */
	@SuppressWarnings("rawtypes")
	private void get(RPCMessage req, RPCMessage res) {

		// parse update params
		final Object id = req.getParams().get("id");

		if (id != null && id instanceof Integer) {

			// get movie by id
			final Long movieId = Long.parseLong(id.toString());
			final Optional<Movie> movie = this.getMovieDAO().findById(movieId);
			String marshMovie = null;
			if (movie.isPresent()) {
				marshMovie = movie.get().toString();
			}

			// return the object
			res.setObj(marshMovie);

		} else if (id != null && (id instanceof Integer[] || id instanceof Iterable)) {

			// get movies id
			final List<Movie> movieList = new ArrayList<Movie>();

			// get objects
			for (final Object element : (Iterable) id) {
				final Long movieId = Long.parseLong(element.toString());
				final Optional<Movie> movie = this.getMovieDAO().findById(movieId);
				if (movie.isPresent()) {
					movieList.add(movie.get());
				}
			}

			// return the objects
			res.setObj(MovieBusterUtils.serializeMovieList(movieList));

		} else {

			// get movies
			final List<Movie> movieList = this.getMovieDAO().findAll();

			// return result
			res.setObj(MovieBusterUtils.serializeMovieList(movieList));

		}
	}

	/**
	 * Update.
	 *
	 * @param req
	 *          the req
	 * @param res
	 *          the res
	 */
	private void update(RPCMessage req, RPCMessage res) {

		// parse update params
		final Movie movie = Movie.fromString(req.getObj());
		final Object id = req.getParams().get("id");

		if (movie != null && id != null && (id instanceof Long || id instanceof Integer)) {

			// get movie by id
			final Long movieId = Long.parseLong(id.toString());
			// create the object on db
			final Movie updatedMovie = this.getMovieDAO().update(movie, movieId);
			// return the created object
			res.setObj(updatedMovie != null ? updatedMovie.toString() : null);

		} else {

			res.setObj(null);
		}
	}

}
