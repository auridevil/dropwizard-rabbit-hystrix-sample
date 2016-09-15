package com.demo.elmozzo.moviebuster.bonus.application;

import com.demo.elmozzo.bundle.RabbitMQBundle;
import com.demo.elmozzo.moviebuster.bonus.config.BonusConfiguration;
import com.demo.elmozzo.moviebuster.bonus.dao.BonusDAO;
import com.demo.elmozzo.moviebuster.bonus.queue.BonusRPCServer;
import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.healtcheck.RabbitMQHealthCheck;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The Class BonusMicroserviceApp. This microservice manage inserts in bonus
 */
public class BonusMicroserviceApp extends Application<BonusConfiguration> {

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new BonusMicroserviceApp().run(args);
	}

	/** The rabbit MQ bundle. */
	private final RabbitMQBundle rabbitMQBundle = new RabbitMQBundle("bonus");

	/** The hibernate bundle. Init hibernate with pojos */
	private final HibernateBundle<BonusConfiguration> hibernateBundle = new HibernateBundle<BonusConfiguration>(Bonus.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(BonusConfiguration configuration) {
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
	public void initialize(Bootstrap<BonusConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
		// migration bundle
		bootstrap.addBundle(new MigrationsBundle<BonusConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(BonusConfiguration conf) {
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
	public void run(BonusConfiguration configuration, Environment environment) throws Exception {

		// create db access
		final BonusDAO bonusDAO = new BonusDAO(this.hibernateBundle.getSessionFactory());

		// rabbit mq
		this.rabbitMQBundle.setMetrics(environment.metrics());
		environment.lifecycle().manage(this.rabbitMQBundle);
		environment.healthChecks().register("rabbitmq", new RabbitMQHealthCheck(this.rabbitMQBundle));

		// create a proxy for the rpc server to use hibernate sessions
		final BonusRPCServer rabbitRequest = new UnitOfWorkAwareProxyFactory(this.hibernateBundle).create(BonusRPCServer.class, BonusRPCServer.getConstructorParamTypes(),
				BonusRPCServer.getConstructorArguments(this.rabbitMQBundle.getRabbitMQService().getChannel(), configuration.getBonusQueue(), bonusDAO));
		// rabbitRequest.consume();

		// register the server
		final Thread t = new Thread(rabbitRequest);
		t.start();
	}

}
