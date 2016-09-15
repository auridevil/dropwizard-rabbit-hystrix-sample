/*
 *
 */
package com.demo.elmozzo.moviebuster.object;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.demo.elmozzo.queue.object.RPCMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class RentResponse.To be returned from a rent request
 */
public class RentResponse {

	/**
	 * Deserialize From string.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the bonus
	 */
	public static RentResponse fromString(String jsonObject) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonObject, RentResponse.class);
		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/** The price lines. */
	private List<ResponseLine> priceLines;

	/** The total price. */
	private double totalPrice;

	/** The total bonus. */
	private int totalBonus;

	/**
	 * Instantiates a new rent response. dummy constructor
	 */
	public RentResponse() {
	}

	/**
	 * Instantiates a new rent response.
	 *
	 * @param priceLines
	 *          the price lines
	 * @param totalPrice
	 *          the total price
	 */
	public RentResponse(List<ResponseLine> priceLines, double totalPrice, int totalBonus) {
		this.setPriceLines(priceLines);
		this.setTotalPrice(totalPrice);
		this.setTotalBonus(totalBonus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj) == false) {
			if (obj.getClass().equals(this.getClass()) == false) {
				return false;
			}
			final RentResponse castedObj = (RentResponse) obj;
			if (castedObj.totalPrice != this.totalPrice) {
				return false;
			}
			if (castedObj.totalBonus != this.totalBonus) {
				return false;
			}
			if (castedObj.priceLines == null && this.priceLines != null) {
				return false;
			}
			if (castedObj.priceLines != null && this.priceLines == null) {
				return false;
			}
			if (this.priceLines != null) { // allow different implementations of List
				for (final ResponseLine localLine : this.priceLines) {
					boolean found = false;
					for (final ResponseLine objLine : castedObj.priceLines) {
						if (localLine.equals(objLine)) {
							found = true;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
			}
		}
		return true;

	}

	/**
	 * Gets the price lines.
	 *
	 * @return the price lines
	 */
	public List<ResponseLine> getPriceLines() {
		return this.priceLines;
	}

	/**
	 * Gets the total bonus.
	 *
	 * @return the total bonus
	 */
	public int getTotalBonus() {
		return this.totalBonus;
	}

	/**
	 * Gets the total price.
	 *
	 * @return the total price
	 */
	public double getTotalPrice() {
		return this.totalPrice;
	}

	/**
	 * Sets the price lines.
	 *
	 * @param priceLines
	 *          the price lines
	 */
	public void setPriceLines(List<ResponseLine> priceLines) {
		this.priceLines = priceLines;
	}

	/**
	 * Sets the total bonus.
	 *
	 * @param totalBonus
	 *          the new total bonus
	 */
	public void setTotalBonus(int totalBonus) {
		this.totalBonus = totalBonus;
	}

	/**
	 * Sets the total price.
	 *
	 * @param totalPrice
	 *          the new total price
	 */
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final String serialized = mapper.writeValueAsString(this);
			return serialized;
		} catch (final Exception ex) {
			LoggerFactory.getLogger(this.getClass()).error("Error in serializing:", ex);
			return ex.getMessage();
		}
	}

	/**
	 * The Class ResponseLine. A single line of a response
	 *
	 */
	public static class ResponseLine {
		private long movieId;
		private long rentId;
		private double price;
		private int bonus = 0;
		private int extraDays = 0;

		public ResponseLine() {

		}

		public ResponseLine(long movieId, double price, int bonus) {
			this.setMovieId(movieId);
			this.setPrice(price);
			this.setBonus(bonus);
		}

		public ResponseLine(long movieId, long rentId, double price) {
			this(movieId, price, 0);
			this.setRentId(rentId);
		}

		@Override
		public boolean equals(Object obj) {
			if (super.equals(obj) == false) {
				if (obj.getClass().equals(this.getClass()) == false) {
					return false;
				}
				final ResponseLine castedObj = (ResponseLine) obj;
				if (this.movieId != castedObj.movieId) {
					return false;
				}
				if (this.rentId != castedObj.rentId) {
					return false;
				}
				if (this.price != castedObj.price) {
					return false;
				}
				if (this.bonus != castedObj.bonus) {
					return false;
				}
				if (this.extraDays != castedObj.extraDays) {
					return false;
				}
			}
			return true;
		}

		public int getBonus() {
			return this.bonus;
		}

		public int getExtraDays() {
			return this.extraDays;
		}

		public long getMovieId() {
			return this.movieId;
		}

		public double getPrice() {
			return this.price;
		}

		public long getRentId() {
			return this.rentId;
		}

		public void setBonus(int bonus) {
			this.bonus = bonus;
		}

		public void setExtraDays(int extraDays) {
			this.extraDays = extraDays;
		}

		public void setMovieId(long movieId) {
			this.movieId = movieId;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public void setRentId(long rentId) {
			this.rentId = rentId;
		}

	}

}
