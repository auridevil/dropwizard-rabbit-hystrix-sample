package com.demo.elmozzo.config;

import io.dropwizard.db.DataSourceFactory;

/**
 * The Interface IDataSourceConfiguration for every configurations that need a
 * DataSourceFactory
 */
public interface IDataSourceConfiguration {

	/**
	 * Gets the data source factory.
	 *
	 * @return the data source factory
	 */
	public DataSourceFactory getDataSourceFactory();

	/**
	 * Sets the data source factory.
	 *
	 * @param dataSourceFactory
	 *          the new data source factory
	 */
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory);

}
