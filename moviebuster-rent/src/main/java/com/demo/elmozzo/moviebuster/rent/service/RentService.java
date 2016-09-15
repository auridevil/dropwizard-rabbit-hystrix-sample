package com.demo.elmozzo.moviebuster.rent.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.demo.elmozzo.moviebuster.object.Bonus;
import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.object.RentMovement.MovementType;
import com.demo.elmozzo.moviebuster.object.RentResponse.ResponseLine;
import com.demo.elmozzo.moviebuster.rent.config.IRentConfiguration;

/**
 * The Class RentService. Calculate everything about renting and bonus points
 */
public class RentService {

	/** The conf. */
	private IRentConfiguration conf;

	/**
	 * Instantiates a new rent service.
	 *
	 * @param conf
	 *          the conf
	 */
	public RentService(IRentConfiguration conf) {
		this.conf = conf;
	}

	/**
	 * Calculate late price.
	 *
	 * @param movie
	 *          the movie
	 * @param daysRent
	 *          the days rent
	 * @param extraDays
	 *          the extra days
	 * @return the value to be payed
	 */
	public double calculateLatePrice(MovieType movieType, int daysRent, int extraDays) {
		if (movieType.equals(MovieType.NEW)) {
			// New releases – Price is <premium price> times number of days rented.
			return this.getConf().getPremiumPrice() * extraDays;

		} else if (movieType.equals(MovieType.REGULAR)) {
			// Regular films – Price is <basic price> for the fist 3 days and then
			// <basic price>
			// times the number of days over 3.
			return this.calculateLate(daysRent, extraDays, this.getConf().getRegularDays());

		} else if (movieType.equals(MovieType.OLD)) {
			// Old film - Price is <basic price> for the fist 5 days and then <basic
			// price>
			// times the number of days over 5
			return this.calculateLate(daysRent, extraDays, this.getConf().getOldDays());

		} else {
			// something went wrong
			return -1;
		}
	}

	/**
	 * Calculate line return.
	 *
	 * @param now
	 *          the now
	 * @param priceLines
	 *          the price lines
	 * @param line
	 *          the line
	 * @param rentLine
	 *          the rent line
	 * @return the double
	 */
	public double calculateLineReturn(final Date now, final Map<Long, ResponseLine> priceLines, final RentMovement line, final RentMovement rentLine) {
		double totalPrice = 0;

		// complete the line
		line.setMovementType(MovementType.RETURN);
		line.setMovieId(rentLine.getMovieId());
		line.setMovieType(rentLine.getMovieType());
		line.setCustomerId(rentLine.getCustomerId());
		line.setMovementDate(line.getMovementDate() != null ? line.getMovementDate() : now);
		line.setRentDays(rentLine.getRentDays());

		// verify if there are any extra days
		final LocalDate rentDate = LocalDate.fromDateFields(rentLine.getMovementDate());
		final LocalDate nowDate = LocalDate.fromDateFields(now);
		final int extraDays = Days.daysBetween(rentDate, nowDate).getDays() - line.getRentDays();
		if (extraDays > 0) {
			line.setExtraDays(extraDays);

			// if any, calculate late price
			final double calculatedPrice = this.calculateLatePrice(line.getMovieType(), line.getRentDays(), extraDays);
			final ResponseLine priceLine = new ResponseLine(line.getMovieId(), line.getRentId(), calculatedPrice);
			priceLine.setExtraDays(extraDays);
			priceLines.put(line.getRentId(), priceLine);
			totalPrice += calculatedPrice;

		} else {
			final ResponseLine priceLine = new ResponseLine(line.getMovieId(), line.getRentId(), 0D);
			priceLine.setExtraDays(0);
			priceLines.put(line.getRentId(), priceLine);

		}
		return totalPrice;
	}

	/**
	 * Calculate all lines rent
	 *
	 * @param rentRequests
	 *          the rent requests
	 * @param moviesData
	 *          the movies data
	 * @param now
	 *          the now
	 * @param priceLines
	 *          the price lines
	 * @param bonusList
	 *          the bonus list
	 * @param totalPrice
	 *          the total price
	 */
	public double calculateLinesRent(final List<RentMovement> rentRequests, final List<Movie> moviesData, final Date now, final Map<Integer, ResponseLine> priceLines, final List<Bonus> bonusList) {

		double totalPrice = 0;
		// convert to a map of movies
		if (moviesData != null && moviesData.size() > 0) {
			final Map<Long, Movie> movieMap = moviesData.stream().collect(Collectors.toMap(el -> el.getId(), el -> el));

			// cycle through request
			for (final RentMovement line : rentRequests) {

				final Movie movie = movieMap.get(line.getMovieId());

				if (movie != null) {
					// complete the line
					line.setMovementType(MovementType.RENT);
					line.setMovieType(movie.getMovieType());
					line.setMovementDate(line.getMovementDate() != null ? line.getMovementDate() : now);

					// create the bonus
					final Bonus bonus = this.getBonus(line.getCustomerId(), line.getMovieType());
					bonusList.add(bonus);

					// calculate the price
					final double calculatedPrice = this.calculatePrice(movie.getMovieType(), line.getRentDays());
					// save line for later returning
					final ResponseLine priceLine = new ResponseLine(line.getMovieId(), calculatedPrice, bonus.getBonusQuantity());
					priceLines.put(line.hashCode(), priceLine);
					totalPrice += calculatedPrice;
				}
			}
		}
		return totalPrice;
	}

	/**
	 * Calculate price.
	 *
	 * @param movie
	 *          the movie
	 * @param days
	 *          the days
	 * @return the double
	 */
	public double calculatePrice(MovieType movieType, int days) {
		if (movieType.equals(MovieType.NEW)) {
			// New releases – Price is <premium price> times number of days rented.
			return this.getConf().getPremiumPrice() * days;

		} else if (movieType.equals(MovieType.REGULAR)) {
			// Regular films – Price is <basic price> for the fist 3 days and then
			// <basic price>
			// times the number of days over 3.
			return this.calculate(days, this.getConf().getRegularDays());

		} else if (movieType.equals(MovieType.OLD)) {
			// Old film - Price is <basic price> for the fist 5 days and then <basic
			// price>
			// times the number of days over 5
			return this.calculate(days, this.getConf().getOldDays());

		} else {
			// something went wrong
			return -1;
		}
	}

	/**
	 * Gets the bonus.
	 *
	 * @param idCustomer
	 *          the id customer
	 * @param movieType
	 *          the movie type
	 * @return the bonus
	 */
	public Bonus getBonus(long idCustomer, MovieType movieType) {
		if (movieType == MovieType.NEW) {
			return new Bonus(idCustomer, this.getConf().getPremiumBonus());
		} else {
			return new Bonus(idCustomer, this.getConf().getBasicBonus());
		}
	}

	/**
	 * Gets the conf.
	 *
	 * @return the conf
	 */
	public IRentConfiguration getConf() {
		return this.conf;
	}

	/**
	 * Sets the conf.
	 *
	 * @param conf
	 *          the new conf
	 */
	public void setConf(IRentConfiguration conf) {
		this.conf = conf;
	}

	/**
	 * Calculate.
	 *
	 * @param days
	 *          the days
	 * @param dayThreshold
	 *          the day threshold
	 * @return the double
	 */
	private double calculate(int days, int dayThreshold) {
		if (days <= dayThreshold) {
			return this.getConf().getBasicPrice();
		} else {
			return this.getConf().getBasicPrice() * (days - (dayThreshold - 1));
		}
	}

	/**
	 * Calculate late.
	 *
	 * @param daysRent
	 *          the days rent
	 * @param extraDays
	 *          the extra days
	 * @param dayThreshold
	 *          the day threshold
	 * @return the double
	 */
	private double calculateLate(int daysRent, int extraDays, int dayThreshold) {
		if (daysRent >= dayThreshold) {
			return this.getConf().getBasicPrice() * extraDays;
		} else {
			int dayToBePayed = extraDays - dayThreshold + daysRent;
			dayToBePayed = dayToBePayed > 0 ? dayToBePayed : 0;
			return this.getConf().getBasicPrice() * dayToBePayed;
		}
	}

}
