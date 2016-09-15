package com.demo.elmozzo.moviebuster.bonus.dao;

import static java.util.Objects.requireNonNull;

import java.sql.Date;

import org.hibernate.SessionFactory;

import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;

import io.dropwizard.hibernate.AbstractDAO;

/**
 * The Class BonusDAO.
 */
public class BonusDAO extends AbstractDAO<Bonus> {

	/**
	 * Instantiates a new bonus DAO.
	 *
	 * @param sessionFactory
	 *          the session factory
	 */
	public BonusDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * Creates the.
	 *
	 * @param bonus
	 *          the bonus
	 * @return the bonus
	 */
	public Bonus create(Bonus bonus) {
		final Date now = MovieBusterUtils.now();
		bonus.setInsertDate(now);
		bonus.setUpdateDate(now);
		return this.persist(requireNonNull(bonus));
	}

}
