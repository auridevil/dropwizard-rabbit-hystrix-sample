package com.demo.elmozzo.moviebuster.object;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

/**
 * Unit tests for {@link User}.
 */
public class UserTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void deserializesFromJSON() throws Exception {

		final User user = new User("elmozzo", "coder");
		user.setId(5L);

		assertThat(MAPPER.readValue(fixture("fixtures/user.json"), User.class)).isEqualTo(user);
	}

	@Test
	public void serializesToJSON() throws Exception {

		final User user = new User("elmozzo", "coder");
		user.setId(5L);

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/user.json"), User.class));
		assertThat(MAPPER.writeValueAsString(user)).isEqualTo(expected);
	}

}
