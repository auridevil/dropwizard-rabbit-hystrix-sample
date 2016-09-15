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

import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.object.RentMovement.MovementType;
import com.demo.elmozzo.moviebuster.object.RentResponse;
import com.demo.elmozzo.moviebuster.object.RentResponse.ResponseLine;
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
 * Unit tests for {@link RentResource}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RentResourceTest {

	/** The mocked RPCClient. */
	private static final RPCClient RPC_MOCK = mock(RPCClient.class);

	/** The resource. */
	@ClassRule
	public static final ResourceTestRule RESOURCE = ResourceTestRule.builder().addResource(new RentResource(RPC_MOCK)).build();

	/** The hystrix test ctx. */
	@Rule
	public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	private String rentRequest;

	private String returnRequest;

	private List<RentMovement> rentRequestObj;

	private List<RentMovement> returnRequestObj;

	private String rentResponse;

	private String returnResponse;

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
	 * Rent movies.
	 *
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
	public void rentMovies() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		when(RPC_MOCK.call(anyString())).thenReturn(this.rentResponse);
		final Response response = RESOURCE.client().target("/rent/new").request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(this.rentRequestObj, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		System.out.println(this.stringCaptor.getValue());
		System.out.println(this.rentRequest);
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.rentRequest);
	}

	/**
	 * Return movies.
	 *
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
	public void returnMovies() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException {
		when(RPC_MOCK.call(anyString())).thenReturn(this.returnResponse);
		final Response response = RESOURCE.client().target("/rent/return").request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(this.returnRequestObj, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.returnRequest);
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

		// rent request
		final RentMovement rm1 = new RentMovement(3L, 5L, MovieType.NEW, MovieBusterUtils.now(), 3, 0, MovementType.RENT);
		rm1.setId(10L);
		List<RentMovement> rmList = new ArrayList<RentMovement>();
		rmList.add(rm1);
		RPCMessage rpcMessage = new RPCMessage("rent", new HashMap<String, Object>(), MovieBusterUtils.serializeRentList(rmList));
		this.rentRequest = rpcMessage.toString();
		this.rentRequestObj = rmList;

		// rent response
		rpcMessage = new RPCMessage();
		List<ResponseLine> rrList = new ArrayList<ResponseLine>();
		ResponseLine line = new ResponseLine();
		line.setMovieId(rm1.getMovieId());
		line.setPrice(30);
		line.setBonus(2);
		line.setRentId(10L);
		rrList.add(line);
		final RentResponse rr1 = new RentResponse(rrList, 30, 2);
		rpcMessage.setObj(rr1.toString());
		this.rentResponse = rpcMessage.toString();

		// return request
		final RentMovement rm2 = new RentMovement(3L, 5L, MovieType.NEW, MovieBusterUtils.now(), 3, 4, MovementType.RETURN);
		rpcMessage = new RPCMessage();
		rmList = new ArrayList<RentMovement>();
		rmList.add(rm2);
		rpcMessage = new RPCMessage("return", new HashMap<String, Object>(), MovieBusterUtils.serializeRentList(rmList));
		this.returnRequest = rpcMessage.toString();
		this.returnRequestObj = rmList;

		// return response
		rpcMessage = new RPCMessage();
		rrList = new ArrayList<ResponseLine>();
		line = new ResponseLine();
		line.setMovieId(rm1.getMovieId());
		line.setPrice(30);
		line.setBonus(2);
		line.setRentId(10L);
		line.setExtraDays(1);
		rrList.add(line);
		final RentResponse rr2 = new RentResponse(rrList, 30, 2);
		rpcMessage.setObj(rr2.toString());
		this.returnResponse = rpcMessage.toString();
	}

}
