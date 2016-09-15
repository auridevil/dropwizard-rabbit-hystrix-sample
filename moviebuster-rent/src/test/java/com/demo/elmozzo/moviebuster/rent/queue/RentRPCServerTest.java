package com.demo.elmozzo.moviebuster.rent.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.demo.elmozzo.queue.object.RPCMessage;

/**
 * Unit tests for {@link RentRPCServer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RentRPCServerTest {

	/** The resource. */
	private static final RentRPCServer RESOURCE_SPY = spy(RentRPCServer.class);

	/** The movie captor. */
	@Captor
	private ArgumentCaptor<RPCMessage> messageCaptor;

	/** The movie captor. */
	@Captor
	private ArgumentCaptor<RPCMessage> responseCaptor;

	@Test
	public void getValueRent() throws Exception {
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) {
				return null;
			}
		}).when(RESOURCE_SPY).rentMovie(any(), any());

		final RPCMessage rpcMessage = new RPCMessage(RentRPCServer.RENT_ACTION, null);
		final RPCMessage rpcResult = new RPCMessage("result", null);
		RESOURCE_SPY.getValue(rpcMessage.toString());

		verify(RESOURCE_SPY).rentMovie(this.messageCaptor.capture(), this.responseCaptor.capture());
		assertThat(this.messageCaptor.getValue()).isNotNull();
		assertThat(this.messageCaptor.getValue().getAction()).isEqualTo(rpcMessage.getAction());
		assertThat(this.messageCaptor.getValue().getException()).isEqualTo(rpcMessage.getException());
		assertThat(this.messageCaptor.getValue().getObj()).isEqualTo(rpcMessage.getObj());
		assertThat(this.messageCaptor.getValue().getParams()).isEqualTo(rpcMessage.getParams());

		assertThat(this.responseCaptor.getValue()).isNotNull();
		assertThat(this.responseCaptor.getValue().getAction()).isEqualTo(rpcResult.getAction());
		assertThat(this.responseCaptor.getValue().getException()).isEqualTo(rpcResult.getException());
		assertThat(this.messageCaptor.getValue().getObj()).isEqualTo(rpcResult.getObj());
		assertThat(this.messageCaptor.getValue().getParams()).isEqualTo(rpcResult.getParams());

		reset(RESOURCE_SPY);
	}

	@Test
	public void getValueReturn() throws Exception {
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) {
				return null;
			}
		}).when(RESOURCE_SPY).returnMovie(any(), any());

		final RPCMessage rpcMessage = new RPCMessage(RentRPCServer.RETURN_ACTION, null);
		final RPCMessage rpcResult = new RPCMessage("result", null);
		RESOURCE_SPY.getValue(rpcMessage.toString());

		verify(RESOURCE_SPY).returnMovie(this.messageCaptor.capture(), this.responseCaptor.capture());
		assertThat(this.messageCaptor.getValue()).isNotNull();
		assertThat(this.messageCaptor.getValue().getAction()).isEqualTo(rpcMessage.getAction());
		assertThat(this.messageCaptor.getValue().getException()).isEqualTo(rpcMessage.getException());
		assertThat(this.messageCaptor.getValue().getObj()).isEqualTo(rpcMessage.getObj());
		assertThat(this.messageCaptor.getValue().getParams()).isEqualTo(rpcMessage.getParams());

		assertThat(this.responseCaptor.getValue()).isNotNull();
		assertThat(this.responseCaptor.getValue().getAction()).isEqualTo(rpcResult.getAction());
		assertThat(this.responseCaptor.getValue().getException()).isEqualTo(rpcResult.getException());
		assertThat(this.messageCaptor.getValue().getObj()).isEqualTo(rpcResult.getObj());
		assertThat(this.messageCaptor.getValue().getParams()).isEqualTo(rpcResult.getParams());
		reset(RESOURCE_SPY);
	}

}
