package com.demo.elmozzo.moviebuster.object;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

/**
 * Unit tests for {@link Bonus}.
 */
public class BonusTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void deserializesFromJSON() throws Exception {
		final Bonus bonus = new Bonus(5L, 2);
		bonus.setId(3);

		assertThat(MAPPER.readValue(fixture("fixtures/bonus.json"), Bonus.class)).isEqualTo(bonus);
	}

	@Test
	public void serializesToJSON() throws Exception {
		final Bonus bonus = new Bonus(5L, 2);
		bonus.setId(3);

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/bonus.json"), Bonus.class));

		assertThat(MAPPER.writeValueAsString(bonus)).isEqualTo(expected);
	}
}
