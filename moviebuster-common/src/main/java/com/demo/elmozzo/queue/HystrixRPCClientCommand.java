package com.demo.elmozzo.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.elmozzo.queue.client.RPCClient;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * The Class HystrixRPCClientCommand.
 *
 * wraps the RPCClient into a hystrix command
 */
public class HystrixRPCClientCommand extends HystrixCommand<String> {

	/** The Constant NOT_AVAILABLE. */
	public static final String GATEWAY_TIMEOUT = null;

	/** The message. */
	private String message;

	/** The client. */
	private RPCClient client;

	/** The log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Instantiates a new hystrix RPC client command.
	 *
	 * @param client
	 *          the client
	 * @param message
	 *          the message
	 * @param hystrixGroupKey
	 *          the hystrix group key
	 */
	public HystrixRPCClientCommand(RPCClient client, String message, String hystrixGroupKey) {
		super(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey));
		this.setMessage(message);
		this.setClient(client);
	}

	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public RPCClient getClient() {
		return this.client;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.netflix.hystrix.HystrixCommand#run()
	 */
	@Override
	public String run() throws Exception {
		this.log.debug("Execute hystrix command");
		return this.getClient().call(this.getMessage());
	}

	/**
	 * Sets the client.
	 *
	 * @param client
	 *          the new client
	 */
	public void setClient(RPCClient client) {
		this.client = client;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *          the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.netflix.hystrix.HystrixCommand#getFallback()
	 */
	@Override
	protected String getFallback() {
		this.log.warn("Fallback executing hystrix command " + this.getMessage());
		return GATEWAY_TIMEOUT; // equals to return null
	}

}
