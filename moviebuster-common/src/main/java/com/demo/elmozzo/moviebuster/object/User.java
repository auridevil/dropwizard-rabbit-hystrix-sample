package com.demo.elmozzo.moviebuster.object;

import java.security.Principal;
import java.util.Objects;

import javax.persistence.Id;

import com.demo.elmozzo.object.PersistedObject;

/**
 * The Class User.
 */
public class User extends PersistedObject implements Principal {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2143856841911358350L;
	private long id;
	private String name;
	private String role;

	/**
	 * Instantiates a new user.
	 */
	public User() {
	}

	/**
	 * Instantiates a new user.
	 *
	 * @param name
	 *          the name
	 * @param role
	 *          the role
	 */
	public User(String name, String role) {
		this.setName(name);
		this.setRole(role);
	}

	/**
	 * The id.
	 *
	 * @return the id
	 */
	@Override
	@Id
	public long getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getRole() {
		return this.role;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#hashCode()
	 */
	@Override
	public int hashCode() {
		// warn potentialy hash(hash(...hash(),..))
		return Objects.hash(super.hashCode(), this.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#setId(long)
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *          the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the role.
	 *
	 * @param role
	 *          the new role
	 */
	public void setRole(String role) {
		this.role = role;
	}

}
