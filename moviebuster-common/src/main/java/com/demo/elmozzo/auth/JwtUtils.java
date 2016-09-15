package com.demo.elmozzo.auth;

import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

import java.io.UnsupportedEncodingException;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.demo.elmozzo.moviebuster.object.User;
import com.demo.elmozzo.config.IJwtConfiguration;
import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;

import io.dropwizard.auth.AuthDynamicFeature;

/**
 * Utils for jwt
 */
public class JwtUtils {

	/**
	 * Generate a jwt token.
	 *
	 * @param conf
	 *          the conf
	 * @return the token
	 * @throws UnsupportedEncodingException
	 *           the unsupported encoding exception
	 * @throws JoseException
	 *           the jose exception
	 */
	public static String generateJwtToken(IJwtConfiguration conf, int expirationTime) throws UnsupportedEncodingException, JoseException {
		final JwtClaims claims = new JwtClaims();
		claims.setSubject("dev-user");
		// claims.setExpirationTimeMinutesInTheFuture(expirationTime);

		final JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(HMAC_SHA256);
		jws.setKey(new HmacKey(conf.getJwtConf().getJwtTokenSecret()));

		return jws.getCompactSerialization();
	}

	/**
	 * Gets the jwt auth dynamic feature.
	 *
	 * @param conf
	 *          the conf
	 * @return the jwt auth dynamic feature
	 * @throws UnsupportedEncodingException
	 *           the unsupported encoding exception
	 */
	public static AuthDynamicFeature getJwtAuthDynamicFeature(IJwtConfiguration conf) throws UnsupportedEncodingException {

		final JwtConsumer consumer = new JwtConsumerBuilder().setAllowedClockSkewInSeconds(30).setVerificationKey(new HmacKey(conf.getJwtConf().getJwtTokenSecret()))
				.setRelaxVerificationKeyValidation().build(); // create the JwtConsumer
																											// instance

		return new AuthDynamicFeature(new JwtAuthFilter.Builder<User>().setJwtConsumer(consumer).setRealm(conf.getJwtConf().getJwtRealm()).setPrefix(conf.getJwtConf().getJwtPrefix())
				.setAuthenticator(new JwtAuthenticator()).buildAuthFilter());
	}

}
