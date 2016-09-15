package com.demo.elmozzo.moviebuster.apigw.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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

import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.queue.client.RPCClient;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandKey;

import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * Unit tests for {@link BonusResource}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BonusResourceTest {

	/** The mocked RPCClient. */
	private static final RPCClient RPC_MOCK = mock(RPCClient.class);

	/** The resource. */
	@ClassRule
	public static final ResourceTestRule RESOURCE = ResourceTestRule.builder().addResource(new BonusResource(RPC_MOCK)).build();

	/** The hystrix test ctx. */
	@Rule
	public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	/** The bonus object. */
	private Bonus bonus;

	/** The bonus create request. */
	private String bonusCreateRequest;

	/** The bonus create response. */
	private String bonusCreateResponse;

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
	 * Insert bonus test.
	 *
	 * @throws Exception
	 *           the exception
	 */
	@Test
	public void insertBonus() throws Exception {
		when(RPC_MOCK.call(anyString())).thenReturn(this.bonusCreateResponse);
		final Response response = RESOURCE.client().target("/bonus").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(this.bonus, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.CREATED);

		verify(RPC_MOCK).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(this.bonusCreateRequest);
	}

	@Before
	public void setup() {
		this.bonus = new Bonus();
		this.bonus.setBonusQuantity(2);
		this.bonus.setCustomerId(5L);
		this.bonus.setId(2L);

		// create request
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		final RPCMessage requestMsg = new RPCMessage("create", argsMap, this.bonus);
		this.bonusCreateRequest = requestMsg.toString();

		// create response
		final RPCMessage rpcMessage = new RPCMessage();
		rpcMessage.setObj(this.bonus.toString());
		this.bonusCreateResponse = rpcMessage.toString();
	}

}
