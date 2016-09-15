package com.demo.elmozzo.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import com.demo.elmozzo.queue.HystrixRPCClientCommand;
import com.demo.elmozzo.queue.client.RPCClient;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;

/**
 * Unit tests for {@link HystrixRPCClientCommand}.
 */
@RunWith(MockitoJUnitRunner.class)
public class HystrixRPCClientCommandTest {

	private static final RPCClient MOCK_RPC_CLIENT = mock(RPCClient.class);

	private static String DEFAULT_REQUEST = "emptyrequest";
	private static String DEFAULT_RESPONSE = "emptystring";

	@Rule
	public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@After
	public void cleanup() {
		// force properties to be clean as well
		ConfigurationManager.getConfigInstance().clear();

		final HystrixCommandKey key = Hystrix.getCurrentThreadExecutingCommand();
		if (key != null) {
			System.out.println("WARNING: Hystrix.getCurrentThreadExecutingCommand() should be null but got: " + key + ". Can occur when calling queue() and never retrieving.");
		}
	}

	/**
	 * Should invoke the fallback when execution fails.
	 *
	 * @throws Exception
	 */
	@Test
	public void shouldFallbackWhenExecutionFails() throws Exception {
		when(MOCK_RPC_CLIENT.call(DEFAULT_REQUEST)).thenThrow(new IOException());
		final HystrixRPCClientCommand command = new HystrixRPCClientCommand(MOCK_RPC_CLIENT, DEFAULT_REQUEST, "hystrixtest");
		final String result = command.execute();
		assertThat(result).isEqualTo(HystrixRPCClientCommand.GATEWAY_TIMEOUT);
		assertThat(command.getFailedExecutionException()).isNotNull();
		assertTrue(command.getExecutionTimeInMilliseconds() > -1);
		assertTrue(command.isFailedExecution());
		this.assertCommandExecutionEvents(command, HystrixEventType.FAILURE, HystrixEventType.FALLBACK_SUCCESS);
		this.assertSaneHystrixRequestLog(1);
		verify(MOCK_RPC_CLIENT).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(DEFAULT_REQUEST);
		reset(MOCK_RPC_CLIENT);
	}

	/**
	 * Test a successful command execution.
	 *
	 * @throws Exception
	 */
	@Test
	public void testExecutionSuccess() throws Exception {
		when(MOCK_RPC_CLIENT.call(DEFAULT_REQUEST)).thenReturn(DEFAULT_RESPONSE);
		final HystrixRPCClientCommand command = new HystrixRPCClientCommand(MOCK_RPC_CLIENT, DEFAULT_REQUEST, "hystrixtest");
		final String result = command.execute();
		assertThat(result).isEqualTo(DEFAULT_RESPONSE);
		assertEquals(null, command.getFailedExecutionException());
		assertNull(command.getExecutionException());
		assertTrue(command.getExecutionTimeInMilliseconds() > -1);
		assertTrue(command.isSuccessfulExecution());
		this.assertCommandExecutionEvents(command, HystrixEventType.SUCCESS);
		this.assertSaneHystrixRequestLog(1);
		verify(MOCK_RPC_CLIENT).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(DEFAULT_REQUEST);
		reset(MOCK_RPC_CLIENT);
	}

	/**
	 * Test a successful command execution (asynchronously).
	 */
	@Test
	public void testQueueSuccess() throws Exception {
		when(MOCK_RPC_CLIENT.call(DEFAULT_REQUEST)).thenReturn(DEFAULT_RESPONSE);
		final HystrixRPCClientCommand command = new HystrixRPCClientCommand(MOCK_RPC_CLIENT, DEFAULT_REQUEST, "hystrixtest");
		final Future<String> future = command.queue();
		assertThat(future.get()).isEqualTo(DEFAULT_RESPONSE);
		assertTrue(command.getExecutionTimeInMilliseconds() > -1);
		assertTrue(command.isSuccessfulExecution());
		this.assertCommandExecutionEvents(command, HystrixEventType.SUCCESS);
		assertNull(command.getExecutionException());
		this.assertSaneHystrixRequestLog(1);
		verify(MOCK_RPC_CLIENT).call(this.stringCaptor.capture());
		assertThat(this.stringCaptor.getValue()).isEqualTo(DEFAULT_REQUEST);
		reset(MOCK_RPC_CLIENT);
	}

	/**
	 * Assert command execution events. (CopyPaste from hystrix repo tests)
	 *
	 * @param command
	 *          the command
	 * @param expectedEventTypes
	 *          the expected event types
	 */
	protected void assertCommandExecutionEvents(HystrixInvokableInfo<?> command, HystrixEventType... expectedEventTypes) {
		boolean emitExpected = false;
		int expectedEmitCount = 0;

		boolean fallbackEmitExpected = false;
		int expectedFallbackEmitCount = 0;

		final List<HystrixEventType> condensedEmitExpectedEventTypes = new ArrayList<HystrixEventType>();

		for (final HystrixEventType expectedEventType : expectedEventTypes) {
			if (expectedEventType.equals(HystrixEventType.EMIT)) {
				if (!emitExpected) {
					// first EMIT encountered, add it to condensedEmitExpectedEventTypes
					condensedEmitExpectedEventTypes.add(HystrixEventType.EMIT);
				}
				emitExpected = true;
				expectedEmitCount++;
			} else if (expectedEventType.equals(HystrixEventType.FALLBACK_EMIT)) {
				if (!fallbackEmitExpected) {
					// first FALLBACK_EMIT encountered, add it to
					// condensedEmitExpectedEventTypes
					condensedEmitExpectedEventTypes.add(HystrixEventType.FALLBACK_EMIT);
				}
				fallbackEmitExpected = true;
				expectedFallbackEmitCount++;
			} else {
				condensedEmitExpectedEventTypes.add(expectedEventType);
			}
		}
		final List<HystrixEventType> actualEventTypes = command.getExecutionEvents();
		assertEquals(expectedEmitCount, command.getNumberEmissions());
		assertEquals(expectedFallbackEmitCount, command.getNumberFallbackEmissions());
		assertEquals(condensedEmitExpectedEventTypes, actualEventTypes);
	}

	/**
	 * Assert sane hystrix request log. (CopyPaste from hystrix repo tests)
	 *
	 * @param numCommands
	 *          the num commands
	 */
	protected void assertSaneHystrixRequestLog(final int numCommands) {
		final HystrixRequestLog currentRequestLog = HystrixRequestLog.getCurrentRequest();

		try {
			assertEquals(numCommands, currentRequestLog.getAllExecutedCommands().size());
			assertFalse(currentRequestLog.getExecutedCommandsAsString().contains("Executed"));
			assertTrue(currentRequestLog.getAllExecutedCommands().iterator().next().getExecutionEvents().size() >= 1);
			// Most commands should have 1 execution event, but fallbacks / responses
			// from cache can cause more than 1. They should never have 0
		} catch (final Throwable ex) {
			System.out.println("Problematic Request log : " + currentRequestLog.getExecutedCommandsAsString() + " , expected : " + numCommands);
			throw new RuntimeException(ex);
		}
	}

}
