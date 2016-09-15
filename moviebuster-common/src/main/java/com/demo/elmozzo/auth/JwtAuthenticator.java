package com.demo.elmozzo.auth;

import java.util.Optional;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import com.demo.elmozzo.moviebuster.object.User;

import io.dropwizard.auth.Authenticator;

/**
 * The Class JwtAuthenticator.
 */
public class JwtAuthenticator implements Authenticator<JwtContext, User> {

	public static final String WS_USER = "ws-user";

	/*
	 * (non-Javadoc)
	 *
	 * @see io.dropwizard.auth.Authenticator#authenticate(java.lang.Object)
	 */
	@Override
	public Optional<User> authenticate(JwtContext context) {
		try {
			return Optional.of(this.getUser(context));
		} catch (final MalformedClaimException e) {
			return Optional.empty();
		}
	}

	/**
	 * Gets the user.
	 *
	 * @param context
	 *          the context
	 * @return the user
	 * @throws MalformedClaimException
	 *           the malformed claim exception
	 */
	public User getUser(JwtContext context) throws MalformedClaimException {
		// TODO: verify auth logics here
		return new User(context.getJwtClaims().getSubject(), WS_USER);
	}
}
