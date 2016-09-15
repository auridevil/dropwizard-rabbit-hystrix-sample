package com.demo.elmozzo.moviebuster.movie.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.demo.elmozzo.config.IDataSourceConfiguration;
import com.demo.elmozzo.config.IRabbitMqConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.codemonastery.dropwizard.rabbitmq.ConnectionFactory;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

/**
 * The Class MovieConfiguration.
 */
public class MovieConfiguration extends Configuration implements IRabbitMqConfiguration, IDataSourceConfiguration {
	/** The rabbit mq conf. */
	@Valid
	@NotNull
	private ConnectionFactory rabbitMqConf = new ConnectionFactory();

	/** The database. */
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	/** The movie queue. */
	@Valid
	@NotNull
	private String movieQueue;

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

	/**
	 * Gets the movie queue.
	 *
	 * @return the movie queue
	 */
	public String getMovieQueue() {
		return this.movieQueue;
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

	/**
	 * Sets the movie queue.
	 *
	 * @param movieQueue
	 *          the new movie queue
	 */
	public void setMovieQueue(String movieQueue) {
		this.movieQueue = movieQueue;
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
