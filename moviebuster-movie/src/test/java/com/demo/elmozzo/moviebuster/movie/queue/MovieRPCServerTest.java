package com.demo.elmozzo.moviebuster.movie.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.demo.elmozzo.moviebuster.movie.dao.MovieDAO;
import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.google.common.collect.ImmutableList;

/**
 * Unit tests for {@link MovieRPCServer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieRPCServerTest {

	/** The mocked DAO. */
	private static final MovieDAO DAO = mock(MovieDAO.class);

	/** The resource. */
	public final MovieRPCServer RESOURCE = new MovieRPCServer(null, "testQ", DAO);

	/** The movie captor. */
	@Captor
	private ArgumentCaptor<Movie> movieCaptor;
	/** The long id captor. */
	@Captor
	private ArgumentCaptor<Long> movieIdCaptor;

	/** The movie object. */
	private Movie movie;

	/** The updated movie. */
	private Movie updatedMovie;

	/**
	 * test Delete movie.
	 *
	 * @throws Exception
	 */
	@Test
	public void deleteMovie() throws Exception {
		when(DAO.delete(anyLong())).thenReturn(this.movie);

		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", this.movie.getId());
		final RPCMessage req = new RPCMessage(MovieRPCServer.DELETE_ACTION, paramMap, null);

		final String response = this.RESOURCE.getValue(req.toString());

		final RPCMessage responseObj = RPCMessage.fromString(response);
		assertThat(responseObj).isNotNull();
		assertThat(responseObj.getObj()).isEqualTo(this.movie.toString());
		assertThat(Movie.fromString(responseObj.getObj())).isEqualTo(this.movie);

		verify(DAO).delete(this.movieIdCaptor.capture());
		assertThat(this.movieIdCaptor.getValue()).isEqualTo(this.movie.getId());
	}

	/**
	 * Gets the all movies.
	 *
	 * @return the all movies
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void getAllMovies() throws Exception {
		final ImmutableList<Movie> movies = ImmutableList.of(this.movie);
		when(DAO.findAll()).thenReturn(movies);

		final Map<String, Object> paramMap = new HashMap<String, Object>();
		final RPCMessage req = new RPCMessage(MovieRPCServer.GET_ACTION, paramMap, null);

		final String response = this.RESOURCE.getValue(req.toString());

		final RPCMessage responseObj = RPCMessage.fromString(response);
		assertThat(responseObj).isNotNull();
		assertThat(responseObj.getObj()).isEqualTo(MovieBusterUtils.serializeMovieList(movies));

		verify(DAO).findAll();
		assertThat(MovieBusterUtils.movieFromStringArray(responseObj.getObj())).containsAll(movies);
	}

	/**
	 * Gets the movie.
	 *
	 * @return the movie
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void getMovie() throws Exception {
		when(DAO.findById(any())).thenReturn(Optional.of(this.movie));

		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", this.movie.getId());
		final RPCMessage req = new RPCMessage(MovieRPCServer.GET_ACTION, paramMap, null);

		final String response = this.RESOURCE.getValue(req.toString());

		final RPCMessage responseObj = RPCMessage.fromString(response);
		assertThat(responseObj).isNotNull();
		assertThat(responseObj.getObj()).isNotNull();
		assertThat(Movie.fromString(responseObj.getObj())).isEqualTo(this.movie);

		verify(DAO).findById(this.movieIdCaptor.capture());
		assertThat(this.movieIdCaptor.getValue()).isEqualTo(this.movie.getId());

	}

	/**
	 * test Insert movie.
	 *
	 * @throws Exception
	 */
	@Test
	public void insertMovie() throws Exception {
		when(DAO.create(any(Movie.class))).thenReturn(this.movie);

		final Map<String, Object> paramMap = new HashMap<String, Object>();
		final RPCMessage req = new RPCMessage(MovieRPCServer.CREATE_ACTION, paramMap, this.movie);

		final String response = this.RESOURCE.getValue(req.toString());

		final RPCMessage responseObj = RPCMessage.fromString(response);
		assertThat(responseObj).isNotNull();
		assertThat(responseObj.getObj()).isNotNull();
		assertThat(Movie.fromString(responseObj.getObj())).isEqualTo(this.movie);

		verify(DAO).create(this.movieCaptor.capture());
		assertThat(this.movieCaptor.getValue()).isEqualTo(this.movie);
	}

	/**
	 * Setup movie obj.
	 */
	@Before
	public void setup() {
		this.movie = new Movie();
		this.movie.setId(1L);
		this.movie.setMovieType(Movie.MovieType.NEW);
		this.movie.setTitle("test-title");
		this.updatedMovie = new Movie();
		this.updatedMovie.setId(1L);
		this.updatedMovie.setTitle("other-title");
		this.updatedMovie.mergeFrom(this.movie);
	}

	/**
	 * Tear down after.
	 */
	@After
	public void tearDown() {
		reset(DAO);
	}

	/**
	 * test update movie.
	 *
	 * @throws Exception
	 */
	@Test
	public void updateMovie() throws Exception {
		when(DAO.update(any(Movie.class), any())).thenReturn(this.updatedMovie);

		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", this.movie.getId());
		final RPCMessage req = new RPCMessage(MovieRPCServer.UPDATE_ACTION, paramMap, this.movie);

		final String response = this.RESOURCE.getValue(req.toString());

		final RPCMessage responseObj = RPCMessage.fromString(response);
		assertThat(responseObj).isNotNull();
		assertThat(responseObj.getObj()).isNotNull();
		assertThat(Movie.fromString(responseObj.getObj())).isEqualTo(this.updatedMovie);

		verify(DAO).update(this.movieCaptor.capture(), this.movieIdCaptor.capture());
		assertThat(this.movieCaptor.getValue()).isEqualTo(this.movie);
		assertThat(this.movieIdCaptor.getValue()).isEqualTo(this.movie.getId());

	}

}
