package com.demo.elmozzo.moviebuster.apigw.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.demo.elmozzo.auth.JwtAuthenticator;
import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.queue.HystrixRPCClientCommand;
import com.demo.elmozzo.queue.client.RPCClient;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.demo.elmozzo.resource.AbstractResource;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Simple post for bonus
 */
@Path("/bonus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BonusResource extends AbstractResource {

	private RPCClient bonusClient;
	private HystrixRPCClientCommand rpcClient;

	/**
	 * Instantiates a new bonus resource.
	 */
	public BonusResource(RPCClient bonusClient) {
		this.setBonusClient(bonusClient);
	}

	/**
	 * Gets the bonus client.
	 *
	 * @return the bonus client
	 */
	public RPCClient getBonusClient() {
		return this.bonusClient;
	}

	/**
	 * Insert bonus.
	 *
	 * @param bonus
	 *          the bonus
	 * @return the bonus created
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ConsumerCancelledException
	 * @throws ShutdownSignalException
	 * @throws ExecutionException
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@RolesAllowed({ JwtAuthenticator.WS_USER })
	public Response insertBonus(@NotNull @Valid Bonus bonus) throws InterruptedException, ExecutionException {

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		final RPCMessage requestMsg = new RPCMessage("create", argsMap, bonus);

		// fire request wrapped in Hystrix command
		final Future<String> result = new HystrixRPCClientCommand(this.getBonusClient(), requestMsg.toString(), "bonusinsert").queue();

		// build results
		return this.buildCreatedResponse(result);

	}

	/**
	 * Sets the bonus client.
	 *
	 * @param bonusClient
	 *          the new bonus client
	 */
	public void setBonusClient(RPCClient bonusClient) {
		this.bonusClient = bonusClient;
	}

}
