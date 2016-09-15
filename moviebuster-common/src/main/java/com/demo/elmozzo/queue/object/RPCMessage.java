/*
 *
 */
package com.demo.elmozzo.queue.object;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The RPCMessage used to exchange data amongst rpc/queue
 */
public class RPCMessage {

	/**
	 * Create an object from error
	 *
	 * @param ex
	 *          the ex
	 * @return the RPC message
	 */
	public static RPCMessage fromError(Exception ex) {
		final RPCMessage msg = new RPCMessage();
		msg.setException(ex);
		return msg;
	}

	/**
	 * Instatiate from string.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the RPC request
	 */
	public static RPCMessage fromString(String jsonObject) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final RPCMessage newObject = mapper.readValue(jsonObject, RPCMessage.class);
			return newObject;
		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/** The action. */
	private String action;

	/** The exception. */
	private Throwable exception;

	/** The obj. */
	private String obj;

	/** The params. */
	private Map<String, Object> params;

	/**
	 * Instantiates a new RPC message. Dummy.
	 */
	public RPCMessage() {

	}

	/**
	 * Instantiates a new RPC message.
	 *
	 * @param action
	 *          the action to execute
	 * @param params
	 *          extra params
	 */
	public RPCMessage(String action, Map<String, Object> params) {
		this.setAction(action);
		this.setParams(params);
	}

	/**
	 * Instantiates a new RPC message.
	 *
	 * @param action
	 *          the action to execute
	 * @param params
	 *          extra params
	 * @param obj
	 *          the object to pass to request
	 */
	public RPCMessage(String action, Map<String, Object> params, Object obj) {
		this(action, params);
		if (obj != null) {
			this.setObj(obj.toString());
		}
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * Gets the exception.
	 *
	 * @return the exception
	 */
	public Throwable getException() {
		return this.exception;
	}

	/**
	 * Gets the obj.
	 *
	 * @return the obj
	 */
	public String getObj() {
		return this.obj;
	}

	/**
	 * Gets the params.
	 *
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return this.params;
	}

	/**
	 * Sets the action.
	 *
	 * @param action
	 *          the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Sets the exception.
	 *
	 * @param exception
	 *          the new exception
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Sets the obj.
	 *
	 * @param obj
	 *          the new obj
	 */
	public void setObj(String obj) {
		this.obj = obj;
	}

	/**
	 * Sets the params.
	 *
	 * @param params
	 *          the params
	 */
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final ObjectMapper mapper = new ObjectMapper();
		// mapper.enableDefaultTyping();
		try {
			final String serialized = mapper.writeValueAsString(this);
			return serialized;
		} catch (final Exception ex) {
			LoggerFactory.getLogger(this.getClass()).error("Error in serializing:", ex);
			return ex.getMessage();
		}
	}

}
