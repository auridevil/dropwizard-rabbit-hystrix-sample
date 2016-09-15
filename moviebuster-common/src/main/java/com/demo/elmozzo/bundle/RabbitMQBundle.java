package com.demo.elmozzo.bundle;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.elmozzo.config.IRabbitMqConfiguration;
import com.demo.elmozzo.queue.RabbitMQService;
import com.codahale.metrics.MetricRegistry;
import com.rabbitmq.client.Connection;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The Class RabbitMQBundle.
 */
public class RabbitMQBundle implements ConfiguredBundle<IRabbitMqConfiguration>, Managed {

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/** The rabbit MQ service. */
	private RabbitMQService rabbitMQService;

	/** The metrics. */
	private MetricRegistry metrics;

	/** The executor service. */
	private ExecutorService executorService;

	/** The name. */
	private final String name;

	/**
	 * Instantiates a new rabbit MQ bundle.
	 *
	 * @param name
	 *          the name
	 */
	public RabbitMQBundle(String name) {
		this.name = name;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.getRabbitMQService().getConnection();
	}

	/**
	 * Gets the executor service.
	 *
	 * @return the executor service
	 */
	public ExecutorService getExecutorService() {
		return this.executorService;
	}

	/**
	 * Gets the metrics.
	 *
	 * @return the metrics
	 */
	public MetricRegistry getMetrics() {
		return this.metrics;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the rabbit MQ service.
	 *
	 * @return the rabbit MQ service
	 */
	public RabbitMQService getRabbitMQService() {
		return this.rabbitMQService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.dropwizard.ConfiguredBundle#initialize(io.dropwizard.setup.Bootstrap)
	 */
	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		// really do nothing here?
		this.log.debug("Rabbit MQ bundle init");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.ConfiguredBundle#run(java.lang.Object,
	 * io.dropwizard.setup.Environment)
	 */
	@Override
	public void run(IRabbitMqConfiguration configuration, Environment environment) throws Exception {
		this.executorService = environment.lifecycle().executorService(this.getName()).build();
		this.rabbitMQService = RabbitMQService.getInstance(this.executorService);
		this.log.info("Rabbit MQ trin' to connect");
		configuration.getRabbitMqConf().buildRetryInitialConnect(environment, this.getExecutorService(), this.getName(), this.rabbitMQService::connected); // do
		// exception
		this.log.info("Rabbit MQ connection phase completed");
	}

	/**
	 * Sets the metrics.
	 *
	 * @param metrics
	 *          the new metrics
	 */
	public void setMetrics(MetricRegistry metrics) {
		this.metrics = metrics;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.lifecycle.Managed#start()
	 */
	@Override
	public void start() throws Exception {
		this.log.debug("RabbitMqBundel started");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.lifecycle.Managed#stop()
	 */
	@Override
	public void stop() throws Exception {
		this.getRabbitMQService().closeConnection();
		this.log.debug("RabbitMqBundel stopped");
	}

}
