package com.demo.elmozzo.moviebuster.rent.config;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.demo.elmozzo.config.IDataSourceConfiguration;
import com.demo.elmozzo.config.IHystrixConfiguration;
import com.demo.elmozzo.config.IRabbitMqConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.codemonastery.dropwizard.rabbitmq.ConnectionFactory;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class RentConfiguration extends Configuration
		implements IRabbitMqConfiguration, IDataSourceConfiguration, IRentConfiguration, IHystrixConfiguration {
	/** The rabbit mq conf. */
	@Valid
	@NotNull
	private ConnectionFactory rabbitMqConf = new ConnectionFactory();

	/** The database. */
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	/** The hystrix conf. */
	@JsonProperty
	@NotNull
	private Map<String, Object> hystrixConf;

	/** The premium price. */
	@Valid
	@NotNull
	private double premiumPrice;

	/** The basic price. */
	@Valid
	@NotNull
	private double basicPrice;

	/** The regular days. */
	@Valid
	@NotNull
	private int regularDays;

	/** The old days. */
	@Valid
	@NotNull
	private int oldDays;

	/** The premium bonus. */
	@Valid
	@NotNull
	private int premiumBonus;

	/** The basic bonus. */
	@Valid
	@NotNull
	private int basicBonus;

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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * getBasicBonus()
	 */
	@Override
	public int getBasicBonus() {
		return this.basicBonus;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * getBasicPrice()
	 */
	@Override
	public double getBasicPrice() {
		return this.basicPrice;
	}

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
	 * @see com.demo.elmozzo.config.IHystrixConfiguration#getHystrixConf()
	 */
	@Override
	public Map<String, Object> getHystrixConf() {
		return this.hystrixConf;
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
	 * @see
	 * com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#getOldDays()
	 */
	@Override
	public int getOldDays() {
		return this.oldDays;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * getPremiumBonus()
	 */
	@Override
	public int getPremiumBonus() {
		return this.premiumBonus;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * getPremiumPrice()
	 */
	@Override
	public double getPremiumPrice() {
		return this.premiumPrice;
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
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * getRegularDays()
	 */
	@Override
	public int getRegularDays() {
		return this.regularDays;
	}

	/**
	 * Gets the rent queue.
	 *
	 * @return the rent queue
	 */
	public String getRentQueue() {
		return this.rentQueue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * setBasicBonus(int)
	 */
	@Override
	public void setBasicBonus(int basicBonus) {
		this.basicBonus = basicBonus;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * setBasicPrice(double)
	 */
	@Override
	public void setBasicPrice(double basicPrice) {
		this.basicPrice = basicPrice;
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
	 * @see
	 * com.demo.elmozzo.config.IHystrixConfiguration#setHystrixConf(java.util.
	 * Map)
	 */
	@Override
	public void setHystrixConf(Map<String, Object> hystrixConf) {
		this.hystrixConf = hystrixConf;
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
	 * @see
	 * com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#setOldDays(
	 * int)
	 */
	@Override
	public void setOldDays(int oldDays) {
		this.oldDays = oldDays;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * setPremiumBonus(int)
	 */
	@Override
	public void setPremiumBonus(int premiumBonus) {
		this.premiumBonus = premiumBonus;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * setPremiumPrice(double)
	 */
	@Override
	public void setPremiumPrice(double premiumPrice) {
		this.premiumPrice = premiumPrice;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration#
	 * setRegularDays(int)
	 */
	@Override
	public void setRegularDays(int regularDays) {
		this.regularDays = regularDays;
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
