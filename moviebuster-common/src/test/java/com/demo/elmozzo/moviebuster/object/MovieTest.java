package com.demo.elmozzo.moviebuster.object;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

/**
 * Unit tests for {@link Movie}.
 */
public class MovieTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void deserializesFromJSON() throws Exception {
		final Movie movie = new Movie("The Snatch", MovieType.REGULAR);
		movie.setId(4);

		assertThat(MAPPER.readValue(fixture("fixtures/movie.json"), Movie.class)).isEqualTo(movie);
	}

	@Test
	public void serializesToJSON() throws Exception {
		final Movie movie = new Movie("The Snatch", MovieType.REGULAR);
		movie.setId(4);

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/movie.json"), Movie.class));

		assertThat(MAPPER.writeValueAsString(movie)).isEqualTo(expected);
	}

}
