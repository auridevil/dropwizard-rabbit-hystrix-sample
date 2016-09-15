package com.demo.elmozzo.auth;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to generate jwt token for dev purposes
 */
public class GenerateTokenCommand extends ConfiguredCommand<JwtConfiguration> {

	/**
	 * Instantiates a new generate token command.
	 */
	public GenerateTokenCommand() {
		super("jwtcreate", "Generate dev jwt token");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.cli.ConfiguredCommand#run(io.dropwizard.setup.Bootstrap,
	 * net.sourceforge.argparse4j.inf.Namespace, io.dropwizard.Configuration)
	 */
	@Override
	protected void run(Bootstrap<JwtConfiguration> bootstrap, Namespace namespace, JwtConfiguration configuration) throws Exception {
		final String token = JwtUtils.generateJwtToken(configuration, 60);
		System.out.println(String.format("%s",token));
	}
}
