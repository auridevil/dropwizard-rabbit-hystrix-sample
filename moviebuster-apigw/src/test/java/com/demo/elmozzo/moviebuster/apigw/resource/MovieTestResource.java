package com.demo.elmozzo.moviebuster.apigw.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;
import com.demo.elmozzo.queue.client.RPCClient;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandKey;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * Unit tests for {@link MovieResource}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MovieTestResource {

	/** The mocked RPCClient. */
	private static final RPCClient RPC_MOCK = mock(RPCClient.class);

	/** The resource. */
	@ClassRule
	public static final ResourceTestRule RESOURCE = ResourceTestRule.builder().addResource(new MovieResource(RPC_MOCK)).build();

	/** The hystrix test ctx. */
	@Rule
	public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	/** The movie object. */
	private Movie movie;

	/** The movie create request. */
	private String movieCreateRequest;

	/** The movie single response. */
	private String movieSingleResponse;

	/** The movie byid request. */
	private String movieByidRequest;

	/** The movie update request. */
	private String movieUpdateRequest;

	/** The movie multi response. */
	private String movieMultiResponse;

	/** The movie request. */
	private String movieRequest;

	private String movieDeleteRequest;

	/**
	 * Cleanup.
	 */
	@After
	public void cleanup() {
		// force properties to be clean as well
		ConfigurationManager.getConfigInstance().clear();

		final HystrixCommandKey key = Hystrix.getCurrentThreadExecutingCommand();
		if (key != null) {
			System.out.println("WARNING: Hystrix.getCurrentThreadExecutingCommand() should be null but got: " + key + ". Can occur when calling queue() and never retrieving.");
		}
		reset(RPC_MOCK);
	}

	/**
	 * Delete movie.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void deleteMovie() throws Exception {
		when(RPC_MOCK.call(anyString())).thenReturn(this.movieSingleResponse);
		final Response response = RESOURCE.client().target("/movie/" + this.movie.getId()).request(MediaType.APPLICATION_JSON_TYPE).delete();

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.movieDeleteRequest);
	}

	/**
	 * Gets the movie by id.
	 *
	 * @return the movie by id
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void getMovieById() throws Exception {
		when(RPC_MOCK.call(anyString())).thenReturn(this.movieSingleResponse);
		final Response response = RESOURCE.client().target("/movie/" + this.movie.getId()).request(MediaType.APPLICATION_JSON_TYPE).get();

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.movieByidRequest);
	}

	/**
	 * Gets the movies.
	 *
	 * @return the movies
	 * @throws ShutdownSignalException
	 *           the shutdown signal exception
	 * @throws ConsumerCancelledException
	 *           the consumer cancelled exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	@Test
	public void getMovies() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		when(RPC_MOCK.call(anyString())).thenReturn(this.movieMultiResponse);
		final Response response = RESOURCE.client().target("/movie").request(MediaType.APPLICATION_JSON_TYPE).get();

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.movieRequest);
	}

	/**
	 * Insert movie test.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void insertMovie() throws Exception {
		when(RPC_MOCK.call(anyString())).thenReturn(this.movieSingleResponse);
		final Response response = RESOURCE.client().target("/movie").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(this.movie, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.CREATED);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.movieCreateRequest);
	}

	/**
	 * Setup.
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ConsumerCancelledException
	 * @throws ShutdownSignalException
	 */
	@Before
	public void setup() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		this.movie = new Movie();
		this.movie.setTitle("The North Face");
		this.movie.setMovieType(MovieType.NEW);
		this.movie.setId(3L);

		final Movie movie2 = new Movie();
		movie2.setTitle("Into the Wild");
		movie2.setMovieType(MovieType.REGULAR);
		movie2.setId(4L);

		// create request
		Map<String, Object> argsMap = new HashMap<String, Object>();
		this.movieCreateRequest = new RPCMessage("create", argsMap, this.movie).toString();

		// update request
		argsMap = new HashMap<String, Object>();
		argsMap.put("id", 3L);
		this.movieUpdateRequest = new RPCMessage("update", argsMap, this.movie).toString();

		// get by id request
		argsMap = new HashMap<String, Object>();
		argsMap.put("id", 3L);
		this.movieByidRequest = new RPCMessage("get", argsMap, null).toString();

		// get by id request
		argsMap = new HashMap<String, Object>();
		this.movieRequest = new RPCMessage("get", argsMap, null).toString();

		// delete request
		argsMap = new HashMap<String, Object>();
		argsMap.put("id", 3L);
		this.movieDeleteRequest = new RPCMessage("delete", argsMap, null).toString();

		// create response
		RPCMessage rpcMessage = new RPCMessage();
		rpcMessage.setObj(this.movie.toString());
		this.movieSingleResponse = rpcMessage.toString();

		// create array response
		rpcMessage = new RPCMessage();
		final List<Movie> mvList = new ArrayList<Movie>();
		mvList.add(this.movie);
		mvList.add(movie2);
		rpcMessage.setObj(MovieBusterUtils.serializeMovieList(mvList));
		this.movieMultiResponse = rpcMessage.toString();

	}

	/**
	 * Update movie.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void updateMovie() throws Exception {
		when(RPC_MOCK.call(anyString())).thenReturn(this.movieSingleResponse);
		final Response response = RESOURCE.client().target("/movie/" + this.movie.getId()).request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.entity(this.movie, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.movieUpdateRequest);
	}

}
