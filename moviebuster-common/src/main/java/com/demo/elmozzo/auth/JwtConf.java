/*
 *
 */
package com.demo.elmozzo.auth;

import java.io.UnsupportedEncodingException;

/**
 * The Class JwtConf.
 */
public class JwtConf {

	/** The jwt token secret. */
	private String jwtTokenSecret;

	/** The jwt realm. */
	private String jwtRealm;

	/** The jwt prefix. */
	private String jwtPrefix;

	/**
	 * Instantiates a new jwt conf.
	 */
	public JwtConf() {
	}

	/**
	 * Instantiates a new jwt conf.
	 *
	 * @param jwtTokenSecret
	 *          the jwt token secret
	 * @param jwtRealm
	 *          the jwt realm
	 * @param jwtPrefix
	 *          the jwt prefix
	 */
	public JwtConf(String jwtTokenSecret, String jwtRealm, String jwtPrefix) {
		this.setJwtTokenSecret(jwtTokenSecret);
		this.setJwtRealm(jwtRealm);
		this.setJwtPrefix(jwtPrefix);
	}

	/**
	 * Gets the jwt prefix.
	 *
	 * @return the jwt prefix
	 */
	public String getJwtPrefix() {
		return this.jwtPrefix;
	}

	/**
	 * Gets the jwt realm.
	 *
	 * @return the jwt realm
	 */
	public String getJwtRealm() {
		return this.jwtRealm;
	}

	/**
	 * Gets the jwt token secret.
	 *
	 * @return the jwt token secret
	 */
	public byte[] getJwtTokenSecret() throws UnsupportedEncodingException {
		return this.jwtTokenSecret.getBytes("UTF-8");
	}

	/**
	 * Sets the jwt prefix.
	 *
	 * @param jwtPrefix
	 *          the new jwt prefix
	 */
	public void setJwtPrefix(String jwtPrefix) {
		this.jwtPrefix = jwtPrefix;
	}

	/**
	 * Sets the jwt realm.
	 *
	 * @param jwtRealm
	 *          the new jwt realm
	 */
	public void setJwtRealm(String jwtRealm) {
		this.jwtRealm = jwtRealm;
	}

	/**
	 * Sets the jwt token secret.
	 *
	 * @param jwtTokenSecret
	 *          the new jwt token secret
	 */
	public void setJwtTokenSecret(String jwtTokenSecret) {
		this.jwtTokenSecret = jwtTokenSecret;
	}

}
