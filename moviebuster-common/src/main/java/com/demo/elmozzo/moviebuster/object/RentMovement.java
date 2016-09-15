package com.demo.elmozzo.moviebuster.object;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.slf4j.LoggerFactory;

import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.demo.elmozzo.object.IPersistedObject;
import com.demo.elmozzo.object.PersistedObject;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class RentMovement. A rent movement is a movement of rent / return of a
 * single movie
 */
@Entity
@Table(name = "rentmovement")
public class RentMovement extends PersistedObject implements IPersistedObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8648674607685146494L;

	/**
	 * Deserialize From string.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the bonus
	 */
	public static RentMovement fromString(String jsonObject) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonObject, RentMovement.class);
		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/** The id. */
	@Id
	@Column(name = "id_movement", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** The movie id. */
	@Column(name = "id_movie", nullable = false)
	@Min(1)
	private long movieId;

	/** The customer id. */
	@Column(name = "id_customer", nullable = false)
	@Min(1)
	private long customerId;

	/** The movie type. De-normalized, from movie */
	@Column(name = "cd_movietype", nullable = false)
	private MovieType movieType;

	/** The movement date. */
	@Column(name = "dt_movement", nullable = false)
	private Date movementDate;

	/** The rent days. */
	@Column(name = "qt_rentdays", nullable = false)
	private int rentDays;

	/** The extra days. */
	@Column(name = "qt_extradays", nullable = true)
	private int extraDays = 0;

	/** The movement type. */
	@Column(name = "cd_movementtype", nullable = false)
	private MovementType movementType;

	/** The related rent - if a return */
	@Column(name = "id_rent", nullable = true)
	private long rentId;

	/** The insert date. */
	@Column(name = "dt_insert", nullable = false)
	private Date insertDate;

	/** The update date. */
	@Column(name = "dt_update", nullable = false)
	private Date updateDate;

	/**
	 * Instantiates a new rent movement. Dummy constructor
	 */
	public RentMovement() {
		super();
	}

	/**
	 * Instantiates a new rent movement.
	 *
	 * @param movieId
	 *          the movie id
	 * @param customerId
	 *          the customer id
	 * @param movieType
	 *          the movie type
	 * @param movementDate
	 *          the movement date
	 * @param rentDays
	 *          the rent days declared
	 * @param extraDays
	 *          the extra days (0 if is a return)
	 * @param movementType
	 *          the movement type
	 */
	public RentMovement(long movieId, long customerId, MovieType movieType, Date movementDate, int rentDays, int extraDays, MovementType movementType) {
		super();
		this.setMovieId(movieId);
		this.setCustomerId(customerId);
		this.setMovieType(movieType);
		this.setMovementDate(movementDate);
		this.setRentDays(rentDays);
		this.setExtraDays(extraDays);
		this.setMovementType(movementType);
	}

	/**
	 * Instantiates a new rent movement.
	 *
	 * @param movieId
	 *          the movie id
	 * @param customerId
	 *          the customer id
	 * @param movieType
	 *          the movie type
	 * @param movementDate
	 *          the movement date
	 * @param rentDays
	 *          the rent days
	 * @param extraDays
	 *          the extra days
	 * @param movementType
	 *          the movement type
	 * @param rentId
	 *          the rent id
	 */
	public RentMovement(long movieId, long customerId, MovieType movieType, Date movementDate, int rentDays, int extraDays, MovementType movementType, long rentId) {
		this(movieId, customerId, movieType, movementDate, rentDays, extraDays, movementType);
		this.setRentId(rentId);
	}

	/**
	 * Gets the customer id.
	 *
	 * @return the customer id
	 */
	public long getCustomerId() {
		return this.customerId;
	}

	/**
	 * Gets the extra days.
	 *
	 * @return the extra days
	 */
	public int getExtraDays() {
		return this.extraDays;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#getId()
	 */
	@Override
	public long getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#getInsertDate()
	 */
	@Override
	public Date getInsertDate() {
		return this.insertDate;
	}

	/**
	 * Gets the movement date.
	 *
	 * @return the movement date
	 */
	public Date getMovementDate() {
		return this.movementDate;
	}

	/**
	 * Gets the movement type.
	 *
	 * @return the movement type
	 */
	@Enumerated(EnumType.ORDINAL)
	public MovementType getMovementType() {
		return this.movementType;
	}

	/**
	 * Gets the movie id.
	 *
	 * @return the movie id
	 */
	public long getMovieId() {
		return this.movieId;
	}

	/**
	 * Gets the movie type.
	 *
	 * @return the movie type
	 */
	@Enumerated(EnumType.ORDINAL)
	public MovieType getMovieType() {
		return this.movieType;
	}

	/**
	 * Gets the rent days.
	 *
	 * @return the rent days
	 */
	public int getRentDays() {
		return this.rentDays;
	}

	/**
	 * Gets the id rent.
	 *
	 * @return the id rent
	 */
	public long getRentId() {
		return this.rentId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#getUpdateDate()
	 */
	@Override
	public Date getUpdateDate() {
		return this.updateDate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#hashCode()
	 */
	@Override
	public int hashCode() {
		// warn potentialy hash(hash(...hash(),..))
		return Objects.hash(super.hashCode(), this.getMovieId(), this.getCustomerId(), this.getMovieType(), this.getMovementDate(), this.getRentDays(), this.getExtraDays(),
				this.getMovementType());
	}

	/**
	 * Sets the customer id.
	 *
	 * @param customerId
	 *          the new customer id
	 */
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	/**
	 * Sets the extra days.
	 *
	 * @param extraDays
	 *          the new extra days
	 */
	public void setExtraDays(int extraDays) {
		this.extraDays = extraDays;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#setInsertDate(java.sql.Date)
	 */
	@Override
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * Sets the movement date.
	 *
	 * @param movementDate
	 *          the new movement date
	 */
	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	/**
	 * Sets the movement type.
	 *
	 * @param movementType
	 *          the new movement type
	 */
	@Enumerated(EnumType.ORDINAL)
	public void setMovementType(MovementType movementType) {
		this.movementType = movementType;
	}

	/**
	 * Sets the movie id.
	 *
	 * @param movieId
	 *          the new movie id
	 */
	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}

	/**
	 * Sets the movie type.
	 *
	 * @param movieType
	 *          the new movie type
	 */
	@Enumerated(EnumType.ORDINAL)
	public void setMovieType(MovieType movieType) {
		this.movieType = movieType;
	}

	/**
	 * Sets the rent days.
	 *
	 * @param rentDays
	 *          the new rent days
	 */
	public void setRentDays(int rentDays) {
		this.rentDays = rentDays;
	}

	/**
	 * Sets the id rent.
	 *
	 * @param idRent
	 *          the new id rent
	 */
	public void setRentId(long rentId) {
		this.rentId = rentId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#setUpdateDate(java.sql.Date)
	 */
	@Override
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * The Enum MovementType.
	 */
	public enum MovementType {
		RENT, RETURN
	}

}
