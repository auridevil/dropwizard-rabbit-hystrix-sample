package com.demo.elmozzo.auth;

import com.demo.elmozzo.config.IJwtConfiguration;

import io.dropwizard.Configuration;

/**
 * The Class JwtConfiguration.
 */
public class JwtConfiguration extends Configuration implements IJwtConfiguration {

	/** The jwt conf. */
	private JwtConf jwtConf;

	/**
	 * Gets the jwt conf.
	 *
	 * @return the jwt conf
	 */
	@Override
	public JwtConf getJwtConf() {
		return this.jwtConf;
	}

	/**
	 * Sets the jwt conf.
	 *
	 * @param jwtConf
	 *          the new jwt conf
	 */
	@Override
	public void setJwtConf(JwtConf jwtConf) {
		this.jwtConf = jwtConf;
	}

}
