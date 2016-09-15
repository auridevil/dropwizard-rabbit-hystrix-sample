package com.demo.elmozzo.config;

import java.util.Map;

/**
 * The Interface IHystrixConfiguration.
 */
public interface IHystrixConfiguration {

	/**
	 * Gets the hystrix conf.
	 *
	 * @return the hystrix conf
	 */
	public Map<String, Object> getHystrixConf();

	/**
	 * Sets the hystrix conf.
	 *
	 * @param hystrixConf
	 *          the hystrix conf
	 */
	public void setHystrixConf(Map<String, Object> hystrixConf);

}
