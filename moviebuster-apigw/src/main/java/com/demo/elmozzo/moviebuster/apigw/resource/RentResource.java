package com.demo.elmozzo.moviebuster.apigw.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.security.RolesAllowed;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;

import com.demo.elmozzo.auth.JwtAuthenticator;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;
import com.demo.elmozzo.queue.HystrixRPCClientCommand;
import com.demo.elmozzo.queue.client.RPCClient;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.demo.elmozzo.resource.AbstractResource;

/**
 * Resources for rent
 */
@Path("/rent")
@Produces(MediaType.APPLICATION_JSON)
public class RentResource extends AbstractResource {

	private static final String[] RENT_REQ_FIELDS = { "movieId", "customerId", "rentDays" };

	private static final String[] RETURN_REQ_FIELDS = { "rentId" };

	private RPCClient rentClient;

	/**
	 * Instantiates a new rent resource.
	 *
	 * @param rentDao
	 *          the rent dao
	 */
	public RentResource(RPCClient rentClient) {
		this.setRentClient(rentClient);
	}

	/**
	 * Gets the rent client.
	 *
	 * @return the rent client
	 */
	public RPCClient getRentClient() {
		return this.rentClient;
	}

	/**
	 * Rent a list of movies
	 *
	 * @param rentRequest
	 *          the rent request
	 * @return the string
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@POST
	@Path("/new")
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response rentMovies(@NotEmpty List<RentMovement> rentRequest) throws InterruptedException, ExecutionException {

		// validate request
		final Object validation = this.validate(rentRequest, RENT_REQ_FIELDS);
		if (validation != null) {
			return badRequest(validation).build();
		}

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		final RPCMessage requestMsg = new RPCMessage("rent", argsMap, MovieBusterUtils.serializeRentList(rentRequest));

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getRentClient(), requestMsg.toString(), "rentnew").queue();

		// build results
		return this.buildResponse(result);
	}

	/**
	 * Return a list of movies
	 *
	 * @param returnRequest
	 *          the return request
	 * @return the rent response
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@POST
	@Path("/return")
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response returnMovies(@NotEmpty List<RentMovement> returnRequest) throws InterruptedException, ExecutionException {

		// validate request
		final Object validation = this.validate(returnRequest, RETURN_REQ_FIELDS);
		if (validation != null) {
			return badRequest(validation).build();
		}

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		final RPCMessage requestMsg = new RPCMessage("return", argsMap, MovieBusterUtils.serializeRentList(returnRequest));

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getRentClient(), requestMsg.toString(), "rentreturn").queue();

		// build results
		return this.buildResponse(result);
	}

	/**
	 * Sets the rent client.
	 *
	 * @param rentClient
	 *          the new rent client
	 */
	public void setRentClient(RPCClient rentClient) {
		this.rentClient = rentClient;
	}

	/**
	 * Validate.
	 *
	 * @param rentRequest
	 *          the rent request
	 * @param validateFields
	 *          the validate fields
	 * @return the string
	 */
	public Object validate(List<RentMovement> rentRequest, String[] validateFields) {
		final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		for (final RentMovement mv : rentRequest) {
			for (final String field : validateFields) {
				if (validator.validateProperty(mv, field).size() > 0) {
					return validator.validateProperty(mv, field);
				}
			}
		}
		return null;
	}

}
