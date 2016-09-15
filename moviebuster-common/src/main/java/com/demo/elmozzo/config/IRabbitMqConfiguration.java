package com.demo.elmozzo.config;

import io.codemonastery.dropwizard.rabbitmq.ConnectionFactory;

/**
 * The Interface IRabbitMqConfiguration.
 */
public interface IRabbitMqConfiguration {

	/**
	 * Gets the rabbit mq conf.
	 *
	 * @return the rabbit mq conf
	 */
	public ConnectionFactory getRabbitMqConf();

	/**
	 * Sets the rabbit mq conf.
	 *
	 * @param rabbitMqConf
	 *          the new rabbit mq conf
	 */
	public void setRabbitMqConf(ConnectionFactory rabbitMqConf);

}
