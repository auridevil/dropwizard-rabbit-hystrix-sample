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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.LoggerFactory;

import com.demo.elmozzo.object.IPersistedObject;
import com.demo.elmozzo.object.PersistedObject;
import com.demo.elmozzo.queue.object.RPCMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Movie Object.
 */
@Entity
@Table(name = "movie")
@NamedQueries({ @NamedQuery(name = "com.demo.elmozzo.moviebuster.object.Movie.findAll", query = "SELECT m FROM Movie m") })
public class Movie extends PersistedObject implements IPersistedObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7502704698141946993L;

	/**
	 * Deserialize From string.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the movie obj
	 */
	public static Movie fromString(String jsonObject) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonObject, Movie.class);
		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/** The id. */
	@Id
	@Column(name = "id_movie", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** The title. */
	@Column(name = "ds_title", nullable = false)
	@NotEmpty
	private String title;

	/** The movie type. */
	@Column(name = "cd_movietype", nullable = false)
	@NotNull
	private MovieType movieType;

	/** The insert date. */
	@Column(name = "dt_insert", nullable = false)
	private Date insertDate;

	/** The update date. */
	@Column(name = "dt_update", nullable = false)
	private Date updateDate;

	/**
	 * Instantiates a new movie. Dummy constructor
	 */
	public Movie() {
		super();
	}

	/**
	 * Instantiates a new movie.
	 */
	public Movie(String title, MovieType movieType) {
		super();
		this.setTitle(title);
		this.setMovieType(movieType);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getId()
	 */
	@Override
	@JsonProperty
	public long getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getInsertDate()
	 */
	@Override
	@JsonProperty
	public Date getInsertDate() {
		return this.insertDate;
	}

	/**
	 * Gets the movie type.
	 *
	 * @return the movie type
	 */
	@JsonProperty
	@Enumerated(EnumType.ORDINAL)
	public MovieType getMovieType() {
		return this.movieType;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	@JsonProperty
	public String getTitle() {
		return this.title;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getUpdateDate()
	 */
	@Override
	@JsonProperty
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
		return Objects.hash(super.hashCode(), this.getTitle(), this.getMovieType());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#setId(long)
	 */
	@Override
	@JsonProperty
	public void setId(long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.demo.elmozzo.object.IPersistedObject#setInsertDate(java.sql.Date)
	 */
	@Override
	@JsonProperty
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * Sets the movie type.
	 *
	 * @param movieType
	 *          the new movie type
	 */
	@Enumerated(EnumType.ORDINAL)
	@JsonProperty
	public void setMovieType(MovieType movieType) {
		this.movieType = movieType;
	}

	/**
	 * Sets the title.
	 *
	 * @param title
	 *          the new title
	 */
	@JsonProperty
	public void setTitle(String title) {
		this.title = title;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.demo.elmozzo.object.IPersistedObject#setUpdateDate(java.sql.Date)
	 */
	@Override
	@JsonProperty
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * The Enum MovieType.
	 */
	public enum MovieType {
		NEW, REGULAR, OLD
	}
}
