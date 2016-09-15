package com.demo.elmozzo.config;

import com.demo.elmozzo.auth.JwtConf;

/**
 * The Interface IJwtConfiguration.
 */
public interface IJwtConfiguration {

	/**
	 * Gets the jwt conf.
	 *
	 * @return the jwt conf
	 */
	public JwtConf getJwtConf();

	/**
	 * Sets the jwt conf.
	 *
	 * @param conf
	 *          the new jwt conf
	 */
	public void setJwtConf(JwtConf conf);

}
