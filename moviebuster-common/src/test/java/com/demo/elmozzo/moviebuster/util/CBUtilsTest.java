package com.demo.elmozzo.moviebuster.util;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.demo.elmozzo.moviebuster.object.Movie;
import com.demo.elmozzo.moviebuster.object.Movie.MovieType;
import com.demo.elmozzo.moviebuster.object.RentMovement;
import com.demo.elmozzo.moviebuster.object.RentMovement.MovementType;

public class CBUtilsTest {

	@Test
	public void deserializesMovieListFromJSON() throws Exception {

		final List<Movie> movieList = new ArrayList<Movie>();
		final Movie mv1 = new Movie("Stargate", MovieType.OLD);
		mv1.setId(2L);
		movieList.add(mv1);
		final Movie mv2 = new Movie("Amarcord", MovieType.OLD);
		mv2.setId(3L);
		movieList.add(mv2);

		final List<Movie> movieListReaded = MovieBusterUtils.movieFromStringArray(fixture("fixtures/movielist.json"));
		assertThat(movieListReaded).isEqualTo(movieList);
	}

	@Test
	public void deserializesRentListFromJSON() throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		final List<RentMovement> rentList = new ArrayList<RentMovement>();
		final RentMovement rm1 = new RentMovement();
		rm1.setId(4L);
		rm1.setMovieId(2L);
		rm1.setCustomerId(2L);
		rm1.setMovieType(MovieType.REGULAR);
		rm1.setRentDays(2);
		rm1.setExtraDays(2);
		rm1.setMovementType(MovementType.RETURN);
		rm1.setRentId(2L);
		rentList.add(rm1);
		final RentMovement rm2 = new RentMovement();
		rm2.setId(5L);
		rm2.setMovieId(4L);
		rm2.setCustomerId(4L);
		rm2.setMovieType(MovieType.REGULAR);
		rm2.setRentDays(4);
		rm2.setExtraDays(4);
		rm2.setMovementType(MovementType.RENT);
		rentList.add(rm2);

		final List<RentMovement> rentListReaded = MovieBusterUtils.rentFromStringArray(fixture("fixtures/rentlist.json"));
		assertThat(rentListReaded).isEqualTo(rentList);

	}

	@Test
	public void now() throws Exception {
		final Date date1 = MovieBusterUtils.now();
		Thread.sleep(10);
		final Date date2 = MovieBusterUtils.now();
		assertThat(date1).isBefore(date2);
	}

}
