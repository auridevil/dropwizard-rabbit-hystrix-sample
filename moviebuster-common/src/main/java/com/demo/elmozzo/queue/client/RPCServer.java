package com.demo.elmozzo.queue.client;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * The Class RabbitMQ RPC Server
 */
public abstract class RPCServer extends AbstractRPC implements Runnable {

	/**
	 * Instantiates a new rabbit MQRPC server.
	 *
	 * @param channel
	 *          the channel name, if any
	 * @param queueName
	 *          the queue name
	 */
	public RPCServer(Channel channel, String queueName) {
		super(channel, queueName);
	}

	/**
	 * Instantiates a new rabbit MQRPC server.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param exchangeName
	 *          the exchange name
	 */
	public RPCServer(Channel channel, String queueName, String exchangeName) {
		super(channel, queueName, exchangeName);
	}

	/**
	 * Consume data on the queue
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ShutdownSignalException
	 *           the shutdown signal exception
	 * @throws ConsumerCancelledException
	 *           the consumer cancelled exception
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public void consume() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

		super.init();
		final com.rabbitmq.client.StringRpcServer server = new com.rabbitmq.client.StringRpcServer(this.getChannel(), this.getQueueName()) {

			// Handle incoming call - as String
			@Override
			public String handleStringCall(String request, AMQP.BasicProperties properties) {
				final String message = request;
				final int len = message != null ? message.length() : -1;

				RPCServer.this.log.info("Request received " + len);
				try {
					// handle request in sub-classes
					return RPCServer.this.getValue(message);
				} catch (final Exception ex) {
					// handle errors
					RPCServer.this.log.error("Error in handling request from queue ", ex);
					return ex.getMessage();
				}
			}
		};

		// start the server
		server.mainloop();
		this.log.info("Awaiting RPC requests");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.consume();
			// just printout all the exceptions
		} catch (final ShutdownSignalException e) {
			e.printStackTrace();
		} catch (final ConsumerCancelledException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the value.
	 *
	 * @param message
	 *          the message
	 * @return the value
	 */
	protected String getValue(String message) throws Exception {
		return "Hello elmozzo - You have to subclass this RPCServer";
		// Override ME
	}

}
