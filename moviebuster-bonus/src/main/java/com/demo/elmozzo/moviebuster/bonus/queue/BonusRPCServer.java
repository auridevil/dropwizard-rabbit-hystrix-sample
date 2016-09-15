package com.demo.elmozzo.moviebuster.bonus.queue;

import org.hibernate.cfg.NotYetImplementedException;

import com.demo.elmozzo.moviebuster.bonus.dao.BonusDAO;
import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.queue.client.RPCServer;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.rabbitmq.client.Channel;

import io.dropwizard.hibernate.UnitOfWork;

/**
 * The Class BonusRPCServer. Sticks togheter db-hibernate and rabbit queue
 *
 */
public class BonusRPCServer extends RPCServer {

	/** The Constant CREATE_ACTION. */
	public static final String CREATE_ACTION = "create";

	/**
	 * Gets the constructor arguments stacked for reflection construction
	 * invokation
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param bonusDAO
	 *          the bonus DAO
	 * @return the constructor arguments
	 */
	public static Object[] getConstructorArguments(Channel channel, String queueName, BonusDAO bonusDAO) {
		final Object[] out = { channel, queueName, bonusDAO };
		return out;
	}

	/**
	 * Gets the constructor param types. Useful for unit of work init
	 *
	 * @return the constructor param types
	 */
	public static Class<?>[] getConstructorParamTypes() {
		final Class<?>[] out = { Channel.class, String.class, BonusDAO.class };
		return out;
	}

	/** The bonus DAO. */
	private BonusDAO bonusDAO;

	/**
	 * Instantiates a new bonus RPC server.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param bonusDAO
	 *          the bonus DAO
	 */
	public BonusRPCServer(Channel channel, String queueName, BonusDAO bonusDAO) {
		super(channel, queueName);
		this.setBonusDAO(bonusDAO);
	}

	/**
	 * Gets the bonus DAO.
	 *
	 * @return the bonus DAO
	 */
	public BonusDAO getBonusDAO() {
		return this.bonusDAO;
	}

	/**
	 * Sets the bonus DAO.
	 *
	 * @param bonusDAO
	 *          the new bonus DAO
	 */
	public void setBonusDAO(BonusDAO bonusDAO) {
		this.bonusDAO = bonusDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.queue.client.RabbitMQRPCServer#getValue(java.lang.
	 * String)
	 */
	@SuppressWarnings("finally")
	@Override
	@UnitOfWork
	protected String getValue(String message) throws Exception {
		// CORE FUNCTIONALITY OF SERVER

		final RPCMessage res = new RPCMessage("result", null);
		try {

			// parse received message
			final RPCMessage req = RPCMessage.fromString(message);

			// create action
			if (req.getAction() != null && req.getAction().equals(CREATE_ACTION)) {

				// parse Bonus object
				final Bonus bonus = Bonus.fromString(req.getObj());
				if (bonus != null) {

					// create the object on db
					final Bonus createdBonus = this.getBonusDAO().create(bonus);
					// return the created object
					res.setObj(createdBonus.toString());
				} else {
					res.setObj(null);
				}
			} else {

				// action not allowed on this ms
				throw new NotYetImplementedException();
			}

		} catch (final Exception ex) {
			this.log.error("Get-Value error", ex);
			res.setException(ex);
		} finally {
			return res.toString();
		}
	}

}
