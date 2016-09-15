package com.demo.elmozzo.moviebuster.rent.queue;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.hibernate.cfg.NotYetImplementedException;

import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.object.RentResponse;
import com.demo.elmozzo.moviebuster.object.RentResponse.ResponseLine;
import com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration;
import com.demo.elmozzo.moviebuster.rent.dao.RentMovementDAO;
import com.demo.elmozzo.moviebuster.rent.service.RentService;
import com.demo.elmozzo.moviebuster.util.MovieBusterUtils;
import com.demo.elmozzo.queue.HystrixRPCClientCommand;
import com.demo.elmozzo.queue.RabbitMQService;
import com.demo.elmozzo.queue.client.RPCClient;
import com.demo.elmozzo.queue.client.RPCServer;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.dropwizard.hibernate.UnitOfWork;

/**
 * The Class RentRPCServer.Sticks togheter db-hibernate and rabbit queue
 */
public class RentRPCServer extends RPCServer {

	/** The Constant CREATE_ACTION. */
	public static final String RENT_ACTION = "rent";
	/** The Constant UPDATE_ACTION. */
	public static final String RETURN_ACTION = "return";

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
	public static Object[] getConstructorArguments(RabbitMQService rabbitService, String queueName, RentMovementDAO rentMovementDAO, RPCClient movieClient, RPCClient bonusClient, IRentConfiguration conf) {
		final Object[] out = { rabbitService, queueName, rentMovementDAO, movieClient, bonusClient, conf };
		return out;
	}

	/**
	 * Gets the constructor param types. Useful for unit of work init
	 *
	 * @return the constructor param types
	 */
	public static Class<?>[] getConstructorParamTypes() {
		final Class<?>[] out = { RabbitMQService.class, String.class, RentMovementDAO.class, RPCClient.class, RPCClient.class, IRentConfiguration.class };
		return out;
	}

	private RabbitMQService rabbitService;

	/** The movie client. */
	private RPCClient movieClient;

	/** The bonus client. */
	private RPCClient bonusClient;

	/** The rentMovement DAO. */
	private RentMovementDAO rentMovementDAO;

	/** The rent service. */
	private RentService rentService;

	IRentConfiguration rentConfiguration;

	/**
	 * Instantiates a new rent RPC server.
	 */
	public RentRPCServer() {
		super(null, null);
		// mocking purpose
	}

	/**
	 * Instantiates a new rent movement RPC server.
	 *
	 * @param channel
	 *          the channel
	 * @param queueName
	 *          the queue name
	 * @param rentMovementDAO
	 *          the rent manager DAO
	 */
	public RentRPCServer(RabbitMQService rabbitService, String queueName, RentMovementDAO rentMovementDAO, RPCClient movieClient, RPCClient bonusClient, IRentConfiguration conf) {
		super(rabbitService.getChannel("rent-channel"), queueName);
		this.setRentMovementDAO(rentMovementDAO);
		this.setMovieClient(movieClient);
		this.setBonusClient(bonusClient);
		this.setRentService(new RentService(conf));
		this.setRabbitService(rabbitService);
		this.rentConfiguration = conf;
	}

	/**
	 * Gets the bonus client.
	 *
	 * @return the bonus client
	 */
	public RPCClient getBonusClient() {
		return this.bonusClient;
	}

	/**
	 * Gets the movie client.
	 *
	 * @return the movie client
	 */
	public RPCClient getMovieClient() {
		return this.movieClient;
	}

	/**
	 * Gets the rabbit service.
	 *
	 * @return the rabbit service
	 */
	public RabbitMQService getRabbitService() {
		return this.rabbitService;
	}

	/**
	 * Gets the rent movement DAO.
	 *
	 * @return the rent movement DAO
	 */
	public RentMovementDAO getRentMovementDAO() {
		return this.rentMovementDAO;
	}

	/**
	 * Gets the rent service.
	 *
	 * @return the rent service
	 */
	public RentService getRentService() {
		return this.rentService;
	}

	/**
	 * Sets the bonus client.
	 *
	 * @param bonusClient
	 *          the new bonus client
	 */
	public void setBonusClient(RPCClient bonusClient) {
		this.bonusClient = bonusClient;
	}

	/**
	 * Sets the movie client.
	 *
	 * @param movieClient
	 *          the new movie client
	 */
	public void setMovieClient(RPCClient movieClient) {
		this.movieClient = movieClient;
	}

	/**
	 * Sets the rabbit service.
	 *
	 * @param rabbitService
	 *          the new rabbit service
	 */
	public void setRabbitService(RabbitMQService rabbitService) {
		this.rabbitService = rabbitService;
	}

	/**
	 * Sets the rent movement DAO.
	 *
	 * @param rentMovementDAO
	 *          the new rent movement DAO
	 */
	public void setRentMovementDAO(RentMovementDAO rentMovementDAO) {
		this.rentMovementDAO = rentMovementDAO;
	}

	/**
	 * Sets the rent service.
	 *
	 * @param rentService
	 *          the new rent service
	 */
	public void setRentService(RentService rentService) {
		this.rentService = rentService;
	}

	/**
	 * Gets the movies details. (ASYNC)
	 *
	 * @param rentRequests
	 *          the rent requests
	 * @return the movies details
	 */
	protected String getMoviesDetails(final List<RentMovement> rentRequests) {
		// create request data mapping to single value the list and serializing
		final Stream<Long> idStream = rentRequests.stream().map(el -> el.getMovieId());
		final Object[] objArray = idStream.toArray();
		final Long[] idArray = new Long[objArray.length];

		for (int i = 0; i < objArray.length; i++) {
			idArray[i] = Long.parseLong(objArray[i].toString());

		}

		this.log.debug("Movie request array: " + idArray);
		// create request obj
		final Map<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("id", idArray);
		final RPCMessage requestMsg = new RPCMessage("get", argsMap);

		// fire request wrapped in Hystrix command
		final String result = new HystrixRPCClientCommand(this.getMovieClient(), requestMsg.toString(), "movieget").execute();
		return result;
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

			// action router
			if (req.getAction() != null && req.getAction().equals(RENT_ACTION)) {
				this.rentMovie(req, res);
			} else if (req.getAction() != null && req.getAction().equals(RETURN_ACTION)) {
				this.returnMovie(req, res);
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

	/**
	 * Rent the movies
	 *
	 * @param req
	 *          the req
	 * @param res
	 *          the res
	 * @throws JsonParseException
	 *           the json parse exception
	 * @throws JsonMappingException
	 *           the json mapping exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	protected void rentMovie(RPCMessage req, RPCMessage res) throws JsonParseException, JsonMappingException, IOException, InterruptedException, ExecutionException {

		final int opId = req.hashCode();
		this.log.info("[" + opId + "] Rent a movie management started ");
		final List<RentMovement> rentRequests = MovieBusterUtils.rentFromStringArray(req.getObj());

		// get the details about the movies
		this.log.info("[" + opId + "] Request movies data to queue");
		final String movieResults = this.getMoviesDetails(rentRequests);

		// get response
		this.log.debug("[" + opId + "] Waiting for movie Results");
		final RPCMessage movieResponseMessage = RPCMessage.fromString(movieResults);
		if (movieResponseMessage.getException() != null) {
			res.setException(movieResponseMessage.getException());
			return;
		}
		this.log.debug("[" + opId + "] Movie results get ok");

		// create data structures
		final List<Movie> moviesData = MovieBusterUtils.movieFromStringArray(movieResponseMessage.getObj());
		final Date now = MovieBusterUtils.now();
		final Map<Integer, ResponseLine> priceLines = new HashMap<Integer, ResponseLine>();
		final List<Bonus> bonusList = new ArrayList<Bonus>();
		double totalPrice = 0;
		int totalBonus = 0;

		// calculate the fees
		this.log.debug("[" + opId + "] Calculate line rents");
		totalPrice = this.getRentService().calculateLinesRent(rentRequests, moviesData, now, priceLines, bonusList);

		// persist bonus (async)
		this.log.debug("[" + opId + "] Persist bonuses");
		totalBonus = this.persistBonus(bonusList);

		// persist rent
		this.log.debug("[" + opId + "] Persist rent movements");
		for (final RentMovement rent : rentRequests) {
			if (rent.getMovementDate() != null) { // if movement date is null, movie
																						// was not present on db, so skip it
				final int key = rent.hashCode();
				final RentMovement created = this.getRentMovementDAO().create(rent);
				final ResponseLine line = priceLines.get(key);
				line.setRentId(created.getId()); // set reference to id
			}
		}

		// return the object
		this.log.debug("[" + opId + "] Create response obj ");
		final RentResponse obj = new RentResponse(new ArrayList<ResponseLine>(priceLines.values()), totalPrice, totalBonus);
		this.log.info("[" + opId + "] Rent a movie management completed");
		res.setObj(obj.toString());
	}

	/**
	 * Return the movies.
	 *
	 * @param req
	 *          the req
	 * @param res
	 *          the res
	 */
	protected void returnMovie(RPCMessage req, RPCMessage res) {

		final int opId = req.hashCode();
		this.log.info("[" + opId + "] Return a movie management started ");
		final List<RentMovement> returnRequests = MovieBusterUtils.rentFromStringArray(req.getObj());

		// Complete data and calculate prices
		final Date now = MovieBusterUtils.now();
		final Map<Long, ResponseLine> priceLines = new HashMap<Long, ResponseLine>();
		double totalPrice = 0;

		this.log.debug("[" + opId + "] Calculate line return fees");
		for (final RentMovement line : returnRequests) {

			// get rent movement
			final RentMovement rentLine = this.getRentMovement(line.getRentId());
			totalPrice += this.getRentService().calculateLineReturn(now, priceLines, line, rentLine);
		}

		// persist return
		this.log.debug("[" + opId + "] Persist rent movements");
		for (final RentMovement rent : returnRequests) {
			this.getRentMovementDAO().create(rent);
		}

		// return the object
		this.log.debug("[" + opId + "] Create response obj ");
		final RentResponse obj = new RentResponse(new ArrayList<ResponseLine>(priceLines.values()), totalPrice, 0);
		this.log.info("[" + opId + "] Return a movie management completed");
		res.setObj(obj.toString());

	}

	/**
	 * Gets the rent movement.
	 *
	 * @param rentId
	 *          the rent id
	 * @return the rent movement
	 */
	private RentMovement getRentMovement(long rentId) {
		final Optional<RentMovement> rm = this.getRentMovementDAO().findById(rentId);
		if (rm.isPresent()) {
			return rm.get();
		} else {
			return null;
		}
	}

	/**
	 * Persist bonus.
	 *
	 * @param bonusList
	 *          the bonus list
	 */
	private int persistBonus(final List<Bonus> bonusList) {
		int totalBonus = 0;
		for (final Bonus bonus : bonusList) {
			// calculate total bonus
			totalBonus += bonus.getBonusQuantity();

			// create request obj
			final Map<String, Object> argsMap = new HashMap<String, Object>();
			final RPCMessage requestMsg = new RPCMessage("create", argsMap, bonus);

			// fire request wrapped in Hystrix command
			try {
				new HystrixRPCClientCommand(this.getBonusClient(), requestMsg.toString(), "bonus-insert").queue();
			} catch (final Exception ex) {
				this.log.error("Error in bonus insert", ex);
			}
		}
		this.log.info("Invocation of bonus create completed");
		return totalBonus;
	}

}
