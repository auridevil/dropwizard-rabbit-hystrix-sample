package com.demo.elmozzo.moviebuster.movie.application;

import com.demo.elmozzo.bundle.RabbitMQBundle;
import com.demo.elmozzo.moviebuster.movie.config.MovieConfiguration;
import com.demo.elmozzo.moviebuster.movie.dao.MovieDAO;
import com.demo.elmozzo.moviebuster.movie.queue.MovieRPCServer;
import com.demo.elmozzo.moviebuster.object.Movie;
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
 * The Class MovieMicroserviceApp.
 */
public class MovieMicroserviceApp extends Application<MovieConfiguration> {

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new MovieMicroserviceApp().run(args);
	}

	/** The rabbit MQ bundle. */
	private final RabbitMQBundle rabbitMQBundle = new RabbitMQBundle("movie");

	/** The hibernate bundle. Init hibernate with pojos */
	private final HibernateBundle<MovieConfiguration> hibernateBundle = new HibernateBundle<MovieConfiguration>(
			Movie.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(MovieConfiguration configuration) {
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
	public void initialize(Bootstrap<MovieConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
				new EnvironmentVariableSubstitutor(false)));
		// migration bundle
		bootstrap.addBundle(new MigrationsBundle<MovieConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(MovieConfiguration conf) {
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
	public void run(MovieConfiguration configuration, Environment environment) throws Exception {

		// create db access
		final MovieDAO movieDAO = new MovieDAO(this.hibernateBundle.getSessionFactory());

		// rabbit mq
		this.rabbitMQBundle.setMetrics(environment.metrics());
		environment.lifecycle().manage(this.rabbitMQBundle);
		environment.healthChecks().register("rabbitmq", new RabbitMQHealthCheck(this.rabbitMQBundle));

		// create a proxy for the rpc server to use hibernate sessions
		final MovieRPCServer rabbitRequest = new UnitOfWorkAwareProxyFactory(this.hibernateBundle)
				.create(MovieRPCServer.class, MovieRPCServer.getConstructorParamTypes(), MovieRPCServer.getConstructorArguments(
						this.rabbitMQBundle.getRabbitMQService().getChannel(), configuration.getMovieQueue(), movieDAO));

		// register the server
		final Thread t = new Thread(rabbitRequest);
		t.start();

	}

}
