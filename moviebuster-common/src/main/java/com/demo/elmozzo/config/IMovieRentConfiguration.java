package com.demo.elmozzo.config;

/**
 * The Interface IMoviePriceConfiguration for every configurations that need a
 * movie price infos
 */
public interface IMovieRentConfiguration {

	/**
	 * Gets the basic price.
	 *
	 * @return the basic price
	 */
	public double getBasicPrice();

	/**
	 * Gets the premium price.
	 *
	 * @return the premium price
	 */
	public double getPremiumPrice();

	/**
	 * Sets the basic price.
	 */
	/**
	 * @param basicPrice
	 */
	public void setBasicPrice(double basicPrice);

	/**
	 * Sets the premium price.
	 */
	/**
	 * @param premiumPrice
	 */
	public void setPremiumPrice(double premiumPrice);
}
