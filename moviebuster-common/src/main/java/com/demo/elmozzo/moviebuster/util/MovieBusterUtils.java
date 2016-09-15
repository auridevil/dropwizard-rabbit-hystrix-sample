package com.demo.elmozzo.moviebuster.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.queue.object.RPCMessage;

/**
 * Static utils for the moviebuster apps
 */
public class MovieBusterUtils {

	/**
	 * From string array.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the list
	 */
	public static List<Movie> movieFromStringArray(String jsonObject) {
		try {

			final List<Movie> rentList = new ArrayList<Movie>();
			final JSONParser parser = new JSONParser();
			final Object obj = parser.parse(jsonObject);
			final JSONArray array = (JSONArray) obj;
			for (int i = 0; i < array.size(); i++) {
				final String line = array.get(i).toString();
				rentList.add(Movie.fromString(line));
			}

			return rentList;

		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/**
	 * Now.
	 *
	 * @return the date
	 */
	public static Date now() {
		return new Date(new java.util.Date().getTime());
	}

	/**
	 * From string array.
	 *
	 * @param jsonObject
	 *          the json object
	 * @return the list
	 */
	public static List<RentMovement> rentFromStringArray(String jsonObject) {
		try {

			final List<RentMovement> rentList = new ArrayList<RentMovement>();

			final JSONParser parser = new JSONParser();
			final Object obj = parser.parse(jsonObject);
			final JSONArray array = (JSONArray) obj;
			for (int i = 0; i < array.size(); i++) {
				final String line = array.get(i).toString();
				rentList.add(RentMovement.fromString(line));
			}

			return rentList;

		} catch (final Exception ex) {
			LoggerFactory.getLogger(RPCMessage.class).error("Error in de-serializing:", ex);
			return null;
		}
	}

	/**
	 * Serialize a list.
	 *
	 * @param list
	 *          the list
	 * @return the string
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String serializeList(List list) {
		String output = "[]";
		if (list != null && list.size() > 0) {
			final JSONArray array = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				array.add(list.get(i).toString());
			}
			output = array.toJSONString();
		}
		return output;
	}

	/**
	 * Serialize the list into json, invoking toString on elements
	 *
	 * @param movieList
	 *          the list
	 * @return the string serialized
	 */
	public static String serializeMovieList(List<Movie> movieList) {
		return serializeList(movieList);
	}

	/**
	 * Serialize list of rent into objects
	 *
	 * @param rentList
	 *          the rent list
	 * @return the string
	 */
	public static String serializeRentList(List<RentMovement> rentList) {
		return serializeList(rentList);
	}
}
