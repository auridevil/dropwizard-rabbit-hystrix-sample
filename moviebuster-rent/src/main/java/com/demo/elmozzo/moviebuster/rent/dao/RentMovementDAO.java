package com.demo.elmozzo.moviebuster.rent.dao;

import static java.util.Objects.requireNonNull;

import java.sql.Date;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * The Class RentMovementDAO.
 */
public class RentMovementDAO extends AbstractDAO<RentMovement> {

	/**
	 * Instantiates a new rent movement DAO.
	 *
	 * @param sessionFactory
	 *          the session factory
	 */
	public RentMovementDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * persist a new rentmovement
	 *
	 * @param rentMovement
	 *          the rent movement to be persisted
	 * @return the rent movement persisted
	 */
	public RentMovement create(RentMovement rentMovement) {
		final Date now = MovieBusterUtils.now();
		rentMovement.setInsertDate(now);
		rentMovement.setUpdateDate(now);
		return this.persist(requireNonNull(rentMovement));
	}

	/**
	 * Find by id.
	 *
	 * @param id
	 *          the id
	 * @return the optional of the rentMovement
	 */
	public Optional<RentMovement> findById(Long id) {
		return Optional.ofNullable(this.get(id));
	}
}
