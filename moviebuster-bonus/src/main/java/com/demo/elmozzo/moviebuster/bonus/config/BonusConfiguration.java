package com.demo.elmozzo.moviebuster.bonus.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.demo.elmozzo.config.IDataSourceConfiguration;
import com.demo.elmozzo.config.IRabbitMqConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.codemonastery.dropwizard.rabbitmq.ConnectionFactory;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

/**
 * The Class BonusConfiguration.
 */
public class BonusConfiguration extends Configuration implements IRabbitMqConfiguration, IDataSourceConfiguration {

	/** The rabbit mq conf. */
	@Valid
	@NotNull
	private ConnectionFactory rabbitMqConf = new ConnectionFactory();

	/** The database. */
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	/** The bonus queue. */
	@Valid
	@NotNull
	private String bonusQueue;

	/**
	 * Gets the bonus queue.
	 *
	 * @return the bonus queue
	 */
	public String getBonusQueue() {
		return this.bonusQueue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.demo.elmozzo.config.IDataSourceConfiguration#getDataSourceFactory()
	 */
	@Override
	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return this.database;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.config.IRabbitMqConfiguration#getRabbitMqConf()
	 */
	@Override
	public ConnectionFactory getRabbitMqConf() {
		return this.rabbitMqConf;
	}

	/**
	 * Sets the bonus queue.
	 *
	 * @param bonusQueue
	 *          the new bonus queue
	 */
	public void setBonusQueue(String bonusQueue) {
		this.bonusQueue = bonusQueue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.demo.elmozzo.config.IDataSourceConfiguration#setDataSourceFactory(io.
	 * dropwizard.db.DataSourceFactory)
	 */
	@Override
	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.database = dataSourceFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.config.IRabbitMqConfiguration#setRabbitMqConf(io.
	 * codemonastery.dropwizard.rabbitmq.ConnectionFactory)
	 */
	@Override
	public void setRabbitMqConf(ConnectionFactory rabbitMqConf) {
		this.rabbitMqConf = rabbitMqConf;
	}

}
