package com.demo.elmozzo.healtcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.elmozzo.bundle.RabbitMQBundle;
import com.codahale.metrics.health.HealthCheck;

/**
 * The Class RabbitMQHealthCheck.
 */
public class RabbitMQHealthCheck extends HealthCheck {
	/** The log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/** The rabbit MQ. */
	private final RabbitMQBundle rabbitMQ;

	/**
	 * Instantiates a new rabbit MQ health check.
	 *
	 * @param rabbitMQ
	 *          the rabbit MQ
	 */
	public RabbitMQHealthCheck(RabbitMQBundle rabbitMQ) {
		this.rabbitMQ = rabbitMQ;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.codahale.metrics.health.HealthCheck#check()
	 */
	@Override
	protected Result check() throws Exception {
		if (this.rabbitMQ.getConnection().isOpen()) {
			this.log.debug("RabbitMq Healtcheck");
			return Result.healthy();
		} else {
			return Result.unhealthy("Connection not open");
		}
	}
}
