package com.demo.elmozzo.queue;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * The Class RabbitMQService manages the rabbitmq, singleton-like
 */
public class RabbitMQService {

	/** The instance of the service */
	private static RabbitMQService instance;

	/** The Constant DEFAULT_CHANNEL. */
	private static final String DEFAULT_CHANNEL = "default";

	/**
	 * Gets the single instance of RabbitMQService. // force to be singleton
	 *
	 * @param executorService
	 *          the executor service
	 * @return single instance of RabbitMQService
	 */
	public static RabbitMQService getInstance(ExecutorService executorService) {
		if (instance == null) {
			instance = new RabbitMQService(executorService);
		}
		return instance;
	}

	/** The channel map. */
	private final Map<String, Channel> channelMap = new HashMap<String, Channel>();

	/** The connection. */
	private Connection connection;

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/** The executor service. */
	private final ExecutorService executorService;

	/**
	 * Instantiates a new rabbit MQ service. NB: Private method
	 *
	 * @param executorService
	 *          the executor service
	 */
	private RabbitMQService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * Close channels.
	 */
	public void closeChannels() {
		for (final Channel ch : this.channelMap.values()) {
			if (ch != null && ch.isOpen()) {
				try {
					ch.close();
				} catch (final Exception e) {
					this.log.info("Error closing channel");
				}
			}
		}
	}

	/**
	 * Close connection.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws TimeoutException
	 *           the timeout exception
	 */
	public void closeConnection() throws IOException, TimeoutException {
		this.closeChannels();
		if (this.connection != null) {
			this.connection.close();
		}
		if (this.executorService != null) {
			this.executorService.shutdown();
		}
	}

	/**
	 * Connected.
	 *
	 * @param connection
	 *          the connection
	 * @throws Exception
	 *           the exception
	 */
	public void connected(Connection connection) throws Exception {
		this.setChannel(connection.createChannel());
		this.setConnection(connection);
		this.getChannel().basicQos(1);
		this.log.info("RabbitMQ client connected to " + connection.getAddress().getHostName());
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public Channel getChannel() {
		return this.getChannel(DEFAULT_CHANNEL);
	}

	/**
	 * Gets the channel.
	 *
	 * @param channelName
	 *          the channel name
	 * @return the channel
	 */
	public Channel getChannel(String channelName) {
		Channel channel = this.channelMap.get(channelName);
		if (channel == null) {
			try {
				// connection can be null, but raise exception
				channel = this.connection.createChannel();
				channel.basicQos(1);
				this.channelMap.put(channelName, channel);
			} catch (final IOException e) {
				this.log.info("Error creating defualt channel");
			}
		}
		return channel;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * Sets the channel.
	 *
	 * @param channel
	 *          the new channel
	 */
	public void setChannel(Channel channel) {
		this.channelMap.put(DEFAULT_CHANNEL, channel);
	}

	/**
	 * Sets the connection.
	 *
	 * @param connection
	 *          the new connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Sets the up exchange.
	 *
	 * @param exchangeName
	 *          the exchange name
	 * @param exchangeType
	 *          the exchange type
	 * @param durableExchange
	 *          the durable exchange
	 * @throws NotYetConnectedException
	 *           the not yet connected exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void setUpExchange(String exchangeName, String exchangeType, boolean durableExchange) throws NotYetConnectedException, IOException {
		this.setUpExchange(exchangeName, exchangeType, durableExchange, DEFAULT_CHANNEL);
	}

	/**
	 * Sets the up exchange.
	 *
	 * @param exchangeName
	 *          the exchange name
	 * @param exchangeType
	 *          the exchange type
	 * @param durableExchange
	 *          the durable exchange
	 * @param channelName
	 *          the channel name
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws NotYetConnectedException
	 *           the not yet connected exception
	 */
	public void setUpExchange(String exchangeName, String exchangeType, boolean durableExchange, String channelName) throws IOException, NotYetConnectedException {
		if (this.getChannel(channelName) != null) {
			this.getChannel(channelName).exchangeDeclare(exchangeName, exchangeType, durableExchange);
		} else {
			throw new NotYetConnectedException();
		}
	}

	/**
	 * Sets the up queue.
	 *
	 * @param exchangeName
	 *          the exchange name
	 * @param queueName
	 *          the queue name
	 * @param passiveQueue
	 *          the passive queue
	 * @param durableQueue
	 *          the durable queue
	 * @param exclusiveQueue
	 *          the exclusive queue
	 * @param argumentsQueue
	 *          the arguments queue
	 * @param routingKey
	 *          the routing key
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws NotYetConnectedException
	 *           the not yet connected exception
	 */
	public void setUpQueue(String exchangeName, String queueName, boolean passiveQueue, boolean durableQueue, boolean exclusiveQueue, Map<String, Object> argumentsQueue, String routingKey)
			throws IOException, NotYetConnectedException {
		this.setUpQueue(exchangeName, queueName, passiveQueue, durableQueue, exclusiveQueue, argumentsQueue, routingKey, DEFAULT_CHANNEL);
	}

	/**
	 * Sets the up queue.
	 *
	 * @param exchangeName
	 *          the exchange name
	 * @param queueName
	 *          the queue name
	 * @param passiveQueue
	 *          the passive queue
	 * @param durableQueue
	 *          the durable queue
	 * @param exclusiveQueue
	 *          the exclusive queue
	 * @param argumentsQueue
	 *          the arguments queue
	 * @param routingKey
	 *          the routing key
	 * @param channelName
	 *          the channel name
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws NotYetConnectedException
	 *           the not yet connected exception
	 */
	public void setUpQueue(String exchangeName, String queueName, boolean passiveQueue, boolean durableQueue, boolean exclusiveQueue, Map<String, Object> argumentsQueue, String routingKey, String channelName)
			throws IOException, NotYetConnectedException {
		if (this.getChannel(channelName) != null) {
			this.getChannel(channelName).queueDeclare(queueName, passiveQueue, durableQueue, exclusiveQueue, argumentsQueue);
			this.getChannel(channelName).queueBind(queueName, exchangeName, routingKey);
		} else {
			throw new NotYetConnectedException();
		}
	}

}
