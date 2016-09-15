package com.demo.elmozzo.moviebuster.object;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.demo.elmozzo.moviebuster.object.RentResponse.ResponseLine;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

/**
 * Unit tests for {@link RentResponse}.
 */
public class RentResponseTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void deserializesFromJSON() throws Exception {

		final List<ResponseLine> lines = new ArrayList<ResponseLine>();
		final ResponseLine line1 = new ResponseLine(2L, (double) 30, 2);
		final ResponseLine line2 = new ResponseLine(2L, 3L, (double) 60);
		line2.setExtraDays(2);
		lines.add(line1);
		lines.add(line2);
		final RentResponse rentResponse = new RentResponse(lines, 90, 2);

		assertThat(MAPPER.readValue(fixture("fixtures/rentresponse.json"), RentResponse.class)).isEqualTo(rentResponse);
	}

	@Test
	public void serializesToJSON() throws Exception {

		final List<ResponseLine> lines = new ArrayList<ResponseLine>();
		final ResponseLine line1 = new ResponseLine(2L, (double) 30, 2);
		final ResponseLine line2 = new ResponseLine(2L, 3L, (double) 60);
		line2.setExtraDays(2);
		lines.add(line1);
		lines.add(line2);
		final RentResponse rentResponse = new RentResponse(lines, 90, 2);

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/rentresponse.json"), RentResponse.class));

		assertThat(MAPPER.writeValueAsString(rentResponse)).isEqualTo(expected);
	}
}
