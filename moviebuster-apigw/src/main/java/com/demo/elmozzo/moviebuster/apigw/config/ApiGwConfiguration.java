package com.demo.elmozzo.moviebuster.apigw.config;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.demo.elmozzo.auth.JwtConf;
import com.demo.elmozzo.config.IHystrixConfiguration;
import com.demo.elmozzo.config.IJwtConfiguration;
import com.demo.elmozzo.config.IRabbitMqConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.codemonastery.dropwizard.rabbitmq.ConnectionFactory;
import io.dropwizard.Configuration;

/**
 * The Class ApiGwConfiguration.
 */
public class ApiGwConfiguration extends Configuration implements IRabbitMqConfiguration, IHystrixConfiguration, IJwtConfiguration {

	/** The jwt conf. */
	private JwtConf jwtConf;

	/** The rabbit mq conf. */
	@Valid
	@NotNull
	private ConnectionFactory rabbitMqConf = new ConnectionFactory();

	/** The hystrix conf. */
	@JsonProperty
	private Map<String, Object> hystrixConf;

	/** The movie queue. */
	@Valid
	@NotNull
	private String movieQueue;

	/** The bonus queue. */
	@Valid
	@NotNull
	private String bonusQueue;

	/** The rent queue. */
	@Valid
	@NotNull
	private String rentQueue;

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
	 * @see com.demo.elmozzo.config.IHystrixConfiguration#getHystrixConf()
	 */
	@Override
	public Map<String, Object> getHystrixConf() {
		return this.hystrixConf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.demo.elmozzo.config.IJwtConfiguration#getJwtConf()
	 */
	@Override
	public JwtConf getJwtConf() {
		return this.jwtConf;
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
	@JsonProperty
	public ConnectionFactory getRabbitMqConf() {
		return this.rabbitMqConf;
	}

	/**
	 * Gets the rent queue.
	 *
	 * @return the rent queue
	 */
	public String getRentQueue() {
		return this.rentQueue;
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
	 * com.demo.elmozzo.config.IHystrixConfiguration#setHystrixConf(java.util.
	 * Map)
	 */
	@Override
	public void setHystrixConf(Map<String, Object> hystrixConf) {
		this.hystrixConf = hystrixConf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.demo.elmozzo.config.IJwtConfiguration#setJwtConf(com.demo.elmozzo.
	 * auth.JwtConf)
	 */
	@Override
	public void setJwtConf(JwtConf jwtConf) {
		this.jwtConf = jwtConf;
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
	@JsonProperty
	public void setRabbitMqConf(ConnectionFactory rabbitMqConf) {
		this.rabbitMqConf = rabbitMqConf;
	}

	/**
	 * Sets the rent queue.
	 *
	 * @param rentQueue
	 *          the new rent queue
	 */
	public void setRentQueue(String rentQueue) {
		this.rentQueue = rentQueue;
	}

}
