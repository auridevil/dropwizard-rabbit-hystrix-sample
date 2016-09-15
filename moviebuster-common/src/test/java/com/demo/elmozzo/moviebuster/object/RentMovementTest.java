package com.demo.elmozzo.moviebuster.object;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.demo.elmozzo.moviebuster.object.RentMovement.MovementType;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

/**
 * Unit tests for {@link RentMovement}.
 */
public class RentMovementTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void deserializesFromJSON() throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		final RentMovement rentMovement = new RentMovement();
		rentMovement.setId(4L);
		rentMovement.setMovieId(2L);
		rentMovement.setCustomerId(2L);
		rentMovement.setMovieType(MovieType.REGULAR);
		rentMovement.setRentDays(2);
		rentMovement.setExtraDays(2);
		rentMovement.setMovementType(MovementType.RETURN);
		rentMovement.setRentId(2L);
		final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
		isoFormat.setTimeZone(TimeZone.getDefault());
		rentMovement.setMovementDate(new Date(isoFormat.parse("2016-09-06").getTime()));

		assertThat(MAPPER.readValue(fixture("fixtures/rentMovement.json"), RentMovement.class)).isEqualTo(rentMovement);
	}

	@Test
	public void serializesToJSON() throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		final RentMovement rentMovement = new RentMovement();
		rentMovement.setId(4L);
		rentMovement.setMovieId(2L);
		rentMovement.setCustomerId(2L);
		rentMovement.setMovieType(MovieType.REGULAR);
		rentMovement.setRentDays(2);
		rentMovement.setExtraDays(2);
		rentMovement.setMovementType(MovementType.RETURN);
		rentMovement.setRentId(2L);
		final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
		isoFormat.setTimeZone(TimeZone.getDefault());
		rentMovement.setMovementDate(new Date(isoFormat.parse("2016-09-06").getTime()));

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/rentMovement.json"), RentMovement.class));

		assertThat(MAPPER.writeValueAsString(rentMovement)).isEqualTo(expected);
	}
}
