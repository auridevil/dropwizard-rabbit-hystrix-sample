package com.demo.elmozzo.moviebuster.object;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.slf4j.LoggerFactory;

import com.demo.elmozzo.object.IPersistedObject;
import com.demo.elmozzo.object.PersistedObject;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class Bonus. Each row is a n-uple of (movie-customer-bonus) (the total is
 * a sum grouped by user)
 */
@Entity
@Table(name = "bonus")
public class Bonus extends PersistedObject implements IPersistedObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2452885370240160616L;

	/**
	 * Deserialize From string.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the bonus
	 */
	public static Bonus fromString(String jsonObject) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonObject, Bonus.class);
		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/**
	 * The id. unuseful field
	 */
	@Id
	@Column(name = "id_bonus", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** The customer id. */
	@Column(name = "id_customer", nullable = false)
	@Min(1)
	private long customerId;

	/** The bonus quantity. */
	@Column(name = "qt_bonus", nullable = false)
	@Min(1)
	private int bonusQuantity;

	/** The insert date. */
	@Column(name = "dt_insert")
	private Date insertDate;

	/** The update date. */
	@Column(name = "dt_update")
	private Date updateDate;

	/**
	 * Instantiates a new bonus. Dummy constructor.
	 */
	public Bonus() {
		super();
	}

	/**
	 * Instantiates a new bonus row.
	 *
	 * @param customerId
	 *          the customer id
	 * @param movieId
	 *          the movie id
	 * @param bonusQuantity
	 *          the bonus quantity
	 */
	public Bonus(Long customerId, int bonusQuantity) {
		super();
		this.setCustomerId(customerId);
		this.setBonusQuantity(bonusQuantity);
	}

	/**
	 * Gets the bonus quantity.
	 *
	 * @return the bonus quantity
	 */
	@JsonProperty
	public int getBonusQuantity() {
		return this.bonusQuantity;
	}

	/**
	 * Gets the customer id.
	 *
	 * @return the customer id
	 */
	@JsonProperty
	public long getCustomerId() {
		return this.customerId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getId()
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
	@JsonProperty
	@Override
	public Date getInsertDate() {
		return this.insertDate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#getUpdateDate()
	 */
	@JsonProperty
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
		return Objects.hash(super.hashCode(), this.getCustomerId(), this.getBonusQuantity());
	}

	/**
	 * Sets the bonus quantity.
	 *
	 * @param bonusQuantity
	 *          the new bonus quantity
	 */
	@JsonProperty
	public void setBonusQuantity(int bonusQuantity) {
		this.bonusQuantity = bonusQuantity;
	}

	/**
	 * Sets the customer id.
	 *
	 * @param customerId
	 *          the new customer id
	 */
	@JsonProperty
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#setId(long)
	 */
	@Override
	@JsonProperty
	public void setId(long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#setInsertDate(java.sql.Date)
	 */
	@Override
	@JsonProperty
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.PersistedObject#setUpdateDate(java.sql.Date)
	 */
	@Override
	@JsonProperty
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
