package com.demo.elmozzo.moviebuster.apigw.application;

import java.security.Principal;

import org.apache.commons.configuration.MapConfiguration;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.zapodot.hystrix.bundle.HystrixBundle;

import com.demo.elmozzo.auth.GenerateTokenCommand;
import com.demo.elmozzo.auth.JwtUtils;
import com.demo.elmozzo.bundle.RabbitMQBundle;
import com.demo.elmozzo.moviebuster.apigw.config.ApiGwConfiguration;
import com.demo.elmozzo.moviebuster.apigw.resource.BonusResource;
import com.demo.elmozzo.moviebuster.apigw.resource.MovieResource;
import com.demo.elmozzo.moviebuster.apigw.resource.RentResource;
import com.demo.elmozzo.healtcheck.RabbitMQHealthCheck;
import com.demo.elmozzo.queue.client.RPCClient;
import com.netflix.config.ConfigurationManager;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * The Class MovieBusterApiGwAppw. Fatjar entry point for dropwizard.
 */
public class MovieBusterApiGwApp extends Application<ApiGwConfiguration> {

	/**
	 * The main method.
	 *
	 * @param args
	 *          the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new MovieBusterApiGwApp().run(args);
	}

	/** The rabbit MQ bundle. */
	private final RabbitMQBundle rabbitMQBundle = new RabbitMQBundle("apigw");

	/**
	 * Initialize.
	 *
	 * @param bootstrap
	 *          the bootstrap object
	 */
	@Override
	public void initialize(Bootstrap<ApiGwConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
		bootstrap.addCommand(new GenerateTokenCommand()); // util for development
		bootstrap.addBundle(this.rabbitMQBundle);
		bootstrap.addBundle(HystrixBundle.withDefaultSettings());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.Application#run(io.dropwizard.Configuration,
	 * io.dropwizard.setup.Environment)
	 */
	@Override
	public void run(ApiGwConfiguration configuration, Environment environment) throws Exception {
		// init hystrix
		ConfigurationManager.install(new MapConfiguration(configuration.getHystrixConf()));

		// auth
		environment.jersey().register(JwtUtils.getJwtAuthDynamicFeature(configuration));
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
		environment.jersey().register(RolesAllowedDynamicFeature.class);

		// rabbit mq
		this.rabbitMQBundle.setMetrics(environment.metrics());
		environment.lifecycle().manage(this.rabbitMQBundle);
		environment.healthChecks().register("rabbitmq", new RabbitMQHealthCheck(this.rabbitMQBundle));

		// enable rabbit consumers
		final RPCClient bonusQueueRabbitClient = new RPCClient(this.rabbitMQBundle.getRabbitMQService().getChannel("bonusch"), configuration.getBonusQueue());
		final RPCClient movieQueueRabbitClient = new RPCClient(this.rabbitMQBundle.getRabbitMQService().getChannel("moviech"), configuration.getMovieQueue());
		final RPCClient rentQueueRabbitClient = new RPCClient(this.rabbitMQBundle.getRabbitMQService().getChannel("rentch"), configuration.getRentQueue());
		bonusQueueRabbitClient.setConnectionService(this.rabbitMQBundle.getRabbitMQService());
		movieQueueRabbitClient.setConnectionService(this.rabbitMQBundle.getRabbitMQService());
		rentQueueRabbitClient.setConnectionService(this.rabbitMQBundle.getRabbitMQService());

		// register resources
		environment.jersey().register(new MovieResource(movieQueueRabbitClient));
		environment.jersey().register(new BonusResource(bonusQueueRabbitClient));
		environment.jersey().register(new RentResource(rentQueueRabbitClient));
	}
}
