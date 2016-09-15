package com.demo.elmozzo.moviebuster.rent.application;

import org.apache.commons.configuration.MapConfiguration;

import com.demo.elmozzo.bundle.RabbitMQBundle;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.rent.config.RentConfiguration;
import com.demo.elmozzo.moviebuster.rent.dao.RentMovementDAO;
import com.demo.elmozzo.moviebuster.rent.queue.RentRPCServer;
import com.demo.elmozzo.healtcheck.RabbitMQHealthCheck;
import com.demo.elmozzo.queue.client.RPCClient;
import com.netflix.config.ConfigurationManager;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class RentMicroserviceApp extends Application<RentConfiguration> {

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new RentMicroserviceApp().run(args);
	}

	/** The rabbit MQ bundle. */
	private final RabbitMQBundle rabbitMQBundle = new RabbitMQBundle("rent");

	/** The hibernate bundle. Init hibernate with pojos */
	private final HibernateBundle<RentConfiguration> hibernateBundle = new HibernateBundle<RentConfiguration>(
			RentMovement.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(RentConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};

	/**
	 * Initialize.
	 *
	 * @param bootstrap
	 *          the bootstrap object
	 */
	@Override
	public void initialize(Bootstrap<RentConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
				new EnvironmentVariableSubstitutor(false)));
		// migration bundle
		bootstrap.addBundle(new MigrationsBundle<RentConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(RentConfiguration conf) {
				return conf.getDataSourceFactory();
			}
		});
		// hibernate bundle
		bootstrap.addBundle(this.hibernateBundle);
		bootstrap.addBundle(this.rabbitMQBundle);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.Application#run(io.dropwizard.Configuration,
	 * io.dropwizard.setup.Environment)
	 */
	@Override
	public void run(RentConfiguration configuration, Environment environment) throws Exception {
		// init hystrix
		ConfigurationManager.install(new MapConfiguration(configuration.getHystrixConf()));

		// create db access
		final RentMovementDAO rentDAO = new RentMovementDAO(this.hibernateBundle.getSessionFactory());

		// rabbit mq
		this.rabbitMQBundle.setMetrics(environment.metrics());
		environment.lifecycle().manage(this.rabbitMQBundle);
		environment.healthChecks().register("rabbitmq", new RabbitMQHealthCheck(this.rabbitMQBundle));

		// create the producer used in the requests
		final RPCClient bonusQueueRabbitClient = new RPCClient(
				this.rabbitMQBundle.getRabbitMQService().getChannel("rent-bonusch"), configuration.getBonusQueue());
		final RPCClient movieQueueRabbitClient = new RPCClient(
				this.rabbitMQBundle.getRabbitMQService().getChannel("rent-moviech-consumer"), configuration.getMovieQueue());

		// create a proxy for the rpc server to use hibernate sessions
		final RentRPCServer rabbitRequest = new UnitOfWorkAwareProxyFactory(this.hibernateBundle).create(
				RentRPCServer.class, RentRPCServer.getConstructorParamTypes(),
				RentRPCServer.getConstructorArguments(this.rabbitMQBundle.getRabbitMQService(), configuration.getRentQueue(),
						rentDAO, movieQueueRabbitClient, bonusQueueRabbitClient, configuration));

		// register the server
		final Thread t = new Thread(rabbitRequest);
		t.start();
	}
}
