package com.demo.elmozzo.queue.client;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.demo.elmozzo.queue.RabbitMQService;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * The Class RabbitMQRPCClient.
 */
public class RPCClient extends AbstractRPC {

	/** The service. */
	private com.rabbitmq.client.RpcClient service;

	/** The connection service. */
	private RabbitMQService connectionService;

	/**
	 * Instantiates a new rabbit MQRPC client.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 */
	public RPCClient(Channel channel, String queueName) {
		super(channel, queueName);
	}

	/**
	 * Instantiates a new rabbit MQRPC client.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param exchangeName
	 *          the exchange name
	 */
	public RPCClient(Channel channel, String queueName, String exchangeName) {
		super(channel, queueName, exchangeName);
	}

	/**
	 * Call the remote
	 *
	 * @param message
	 *          the message
	 * @param expiration
	 *          the expiration
	 * @return the string
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ShutdownSignalException
	 *           the shutdown signal exception
	 * @throws ConsumerCancelledException
	 *           the consumer cancelled exception
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public String call(String message) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		this.log.info("Init client on queue " + this.getQueueName());
		this.init();

		this.log.info(String.format("Invoking channel:[%d] exchange:[%s] queue:[%s] messageLength:[%s]", this.getChannel().getChannelNumber(), this.getExchangeName(),
				this.getQueueName(), message.length()));

		try {

			// execute
			return this.getService().stringCall(message);

		} catch (final TimeoutException ex) {
			this.log.warn(String.format("Timout on channel:[%d] exchange:[%s] queue:[%s] messageLength:[%s]", this.getChannel().getChannelNumber(), this.getExchangeName(),
					this.getQueueName(), message.length()));
			// fail silently - hystrix manages the timeout exception
		} catch (final java.io.EOFException ex) {
			this.log.warn(String.format("Queue not available on channel:[%d] exchange:[%s] queue:[%s] messageLength:[%s]", this.getChannel().getChannelNumber(), this.getExchangeName(),
					this.getQueueName(), message.length()));
			// fail silently - hystrix manages the timeout exception
		}
		return null;
	}

	/**
	 * Gets the connection service.
	 *
	 * @return the connection service
	 */
	public RabbitMQService getConnectionService() {
		return this.connectionService;
	}

	/**
	 * Sets the connection service.
	 *
	 * @param connectionService
	 *          the new connection service
	 */
	public void setConnectionService(RabbitMQService connectionService) {
		this.connectionService = connectionService;
	}

	/**
	 * Sets the service.
	 *
	 * @param service
	 *          the new service
	 */
	public void setService(com.rabbitmq.client.RpcClient service) {
		this.service = service;
	}

	/**
	 * Gets the service.
	 *
	 * @return the service
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	private com.rabbitmq.client.RpcClient getService() throws IOException {
		if (this.service == null) {
			this.log.info(String.format("Creating inner rpcClient object for channel:[%d] exchange:[%s] queue:[%s]", this.getChannel().getChannelNumber(), this.getExchangeName(),
					this.getQueueName()));

			try {
				this.service = new com.rabbitmq.client.RpcClient(this.getChannel(), this.getExchangeName(), this.getQueueName());
			} catch (final AlreadyClosedException ex) {
				if (this.getConnectionService() != null) {
					this.setChannel(this.connectionService.getChannel(UUID.randomUUID().toString()));
					this.log.debug("Created a new channel for AlreadyClosedException");
					this.service = new com.rabbitmq.client.RpcClient(this.getChannel(), this.getExchangeName(), this.getQueueName());
				}
			}
		}
		return this.service;
	}

}
