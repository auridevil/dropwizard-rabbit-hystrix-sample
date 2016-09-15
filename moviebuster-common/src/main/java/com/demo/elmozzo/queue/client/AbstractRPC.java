package com.demo.elmozzo.queue.client;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;

/**
 * The abstract object for publisher (client) and consumer (server) RPC
 */
public abstract class AbstractRPC {

	/** The exchange name. */
	private String exchangeName = "";

	/** The channel. */
	private Channel channel;

	/** The queue name. */
	private String queueName;

	/** The durable. */
	private boolean durable = true;

	/** The exclusive. */
	private boolean exclusive = false;

	/** The autodelete. */
	private boolean autodelete = false;

	/** The init. */
	private boolean init = false;

	/** The log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Instantiates a new abstract rabbitMQ RPC.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 */
	public AbstractRPC(Channel channel, String queueName) {
		this.setChannel(channel);
		this.setQueueName(queueName);
	}

	/**
	 * Instantiates a new abstract rabbitMQ RPC.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param exchangeName
	 *          the exchange name
	 */
	public AbstractRPC(Channel channel, String queueName, String exchangeName) {
		this(channel, queueName);
		this.setExchangeName(exchangeName);
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public Channel getChannel() {
		return this.channel;
	}

	/**
	 * Gets the exchange name.
	 *
	 * @return the exchange name
	 */
	public String getExchangeName() {
		return this.exchangeName;
	}

	/**
	 * Gets the queue name.
	 *
	 * @return the queue name
	 */
	public String getQueueName() {
		return this.queueName;
	}

	/**
	 * Inits the.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void init() throws IOException {
		if (!this.init) {
			this.getChannel().queueDeclare(this.getQueueName(), this.isDurable(), this.isExclusive(), this.isAutodelete(),
					this.getArgsMap());
			this.init = true;
		}
	}

	/**
	 * Checks if is autodelete.
	 *
	 * @return true, if is autodelete
	 */
	public boolean isAutodelete() {
		return this.autodelete;
	}

	/**
	 * Checks if is durable.
	 *
	 * @return true, if is durable
	 */
	public boolean isDurable() {
		return this.durable;
	}

	/**
	 * Checks if is exclusive.
	 *
	 * @return true, if is exclusive
	 */
	public boolean isExclusive() {
		return this.exclusive;
	}

	/**
	 * Sets the autodelete.
	 *
	 * @param autodelete
	 *          the new autodelete
	 */
	public void setAutodelete(boolean autodelete) {
		this.autodelete = autodelete;
	}

	/**
	 * Sets the channel.
	 *
	 * @param channel
	 *          the new channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Sets the durable.
	 *
	 * @param durable
	 *          the new durable
	 */
	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	/**
	 * Sets the exchange name.
	 *
	 * @param exchangeName
	 *          the new exchange name
	 */
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	/**
	 * Sets the exclusive.
	 *
	 * @param exclusive
	 *          the new exclusive
	 */
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	/**
	 * Sets the queue name.
	 *
	 * @param queueName
	 *          the new queue name
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Gets the args map.
	 *
	 * @return the args map
	 */
	protected Map<String, Object> getArgsMap() {
		return null; // for future implementations
	}
}
