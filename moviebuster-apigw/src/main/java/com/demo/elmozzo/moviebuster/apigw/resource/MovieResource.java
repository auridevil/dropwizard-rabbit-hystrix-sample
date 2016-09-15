package com.demo.elmozzo.moviebuster.apigw.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.demo.elmozzo.auth.JwtAuthenticator;
import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.queue.HystrixRPCClientCommand;
import com.demo.elmozzo.queue.client.RPCClient;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.demo.elmozzo.resource.AbstractResource;

import io.dropwizard.jersey.params.LongParam;

/**
 *
 * CRUD for the movie db
 */
@Path("/movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource extends AbstractResource {

	private RPCClient movieClient;

	/**
	 * Instantiates a new bonus resource.
	 */
	public MovieResource(RPCClient movieClient) {
		this.setMovieClient(movieClient);
	}

	/**
	 * Delete a movie by id.
	 *
	 * @param movieIdParam
	 *          the movie id param
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@DELETE
	@Path("/{movieId}")
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response deleteMovie(@Min(1) @PathParam("movieId") LongParam movieIdParam) throws InterruptedException, ExecutionException {

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("id", movieIdParam.get());
		final RPCMessage requestMsg = new RPCMessage("delete", argsMap, null);

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getMovieClient(), requestMsg.toString(), "moviedelete").queue();

		// build results
		return this.buildResponse(result);
	}

	/**
	 * Gets the movies.
	 *
	 * @param movieIdParam
	 *          the movie id param
	 * @return the movie
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GET
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response getAllMovies() throws InterruptedException, ExecutionException {

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		final RPCMessage requestMsg = new RPCMessage("get", argsMap, null);

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getMovieClient(), requestMsg.toString(), "moviegetall").queue();

		// build results
		return this.buildResponseFromArray(result);

	}

	/**
	 * Gets the movie by id.
	 *
	 * @param movieIdParam
	 *          the movie id param
	 * @return the movie
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@GET
	@Path("/{movieId}")
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response getMovie(@Min(1) @PathParam("movieId") LongParam movieIdParam) throws InterruptedException, ExecutionException {

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("id", movieIdParam.get());
		final RPCMessage requestMsg = new RPCMessage("get", argsMap, null);

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getMovieClient(), requestMsg.toString(), "movieget").queue();

		// build results
		return this.buildResponse(result);

	}

	/**
	 * Gets the movie client.
	 *
	 * @return the movie client
	 */
	public RPCClient getMovieClient() {
		return this.movieClient;
	}

	/**
	 * Insert movie.
	 *
	 * @param movie
	 *          the movie
	 * @return the movie
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response insertMovie(@NotNull @Valid Movie movie) throws InterruptedException, ExecutionException {

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		final RPCMessage requestMsg = new RPCMessage("create", argsMap, movie);

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getMovieClient(), requestMsg.toString(), "movieinsert").queue();

		// build results
		return this.buildCreatedResponse(result);

	}

	/**
	 * Sets the movie client.
	 *
	 * @param movieClient
	 *          the new movie client
	 */
	public void setMovieClient(RPCClient movieClient) {
		this.movieClient = movieClient;
	}

	/**
	 * Update movie.
	 *
	 * @param movie
	 *          the movie
	 * @return the movie
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	@PUT
	@Path("/{movieId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response updateMovie(@NotNull Movie movie, @Min(1) @PathParam("movieId") LongParam movieIdParam) throws InterruptedException, ExecutionException {

		// custom control
		if (movie.getMovieType() == null && movie.getTitle() == null) {
			return badRequest("Field not updatable").build();
		}

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("id", movieIdParam.get());
		final RPCMessage requestMsg = new RPCMessage("update", argsMap, movie);

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getMovieClient(), requestMsg.toString(), "movieupdate").queue();

		// build results
		return this.buildResponse(result);

	}
}
