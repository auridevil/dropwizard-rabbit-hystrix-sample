package com.demo.elmozzo.queue.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Unit tests for {@link RPCClient}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RPCClientTest {

	private static final String QUEUE_NAME = "testQ";
	private static final String EXCHANGE_NAME = "exQ";
	private static final String RESPONSE = "response";
	private static final String MESSAGE = "message";
	private final Channel MOCKED_CHANNEL = mock(Channel.class);
	private final RPCClient RESOURCE = new RPCClient(this.MOCKED_CHANNEL, QUEUE_NAME, EXCHANGE_NAME);
	private final com.rabbitmq.client.RpcClient MOCKED_SERVICE = mock(com.rabbitmq.client.RpcClient.class);
	@Captor
	private ArgumentCaptor<String> messageCaptor;
	@Captor
	private ArgumentCaptor<String> queueCaptor;

	/**
	 * Cleanup.
	 */
	@After
	public void cleanup() {
		reset(this.MOCKED_CHANNEL);
		reset(this.MOCKED_SERVICE);
	}

	/**
	 * Should call correctly.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ShutdownSignalException
	 *           the shutdown signal exception
	 * @throws ConsumerCancelledException
	 *           the consumer cancelled exception
	 * @throws InterruptedException
	 *           the interrupted exception
	 * @throws TimeoutException
	 *           the timeout exception
	 */
	@Test
	public void shouldCallCorrectly() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, TimeoutException {
		when(this.MOCKED_CHANNEL.queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), any())).thenReturn(null);
		when(this.MOCKED_SERVICE.stringCall(anyString())).thenReturn(RESPONSE);
		this.RESOURCE.setService(this.MOCKED_SERVICE);
		final String response = this.RESOURCE.call(MESSAGE);
		assertThat(response).isEqualTo(RESPONSE);
		verify(this.MOCKED_CHANNEL).queueDeclare(this.queueCaptor.capture(), anyBoolean(), anyBoolean(), anyBoolean(), any());
		assertThat(this.queueCaptor.getValue()).isEqualTo(QUEUE_NAME);
		verify(this.MOCKED_SERVICE).stringCall(this.messageCaptor.capture());
		assertThat(this.messageCaptor.getValue()).isEqualTo(MESSAGE);
	}

}
