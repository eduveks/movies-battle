package br.com.letscode.moviesbattle;

import br.com.letscode.moviesbattle.service.OMDbAPIService;
import br.com.letscode.moviesbattle.util.BaseTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

public class OMDbTest extends BaseTest {
    @Test
    public void next() {
        OMDbAPIService.Movie movie = restClient()
                .when()
                .get("/api/omdb/next")
                .then()
                .statusCode(is(200))
                .extract().as(OMDbAPIService.Movie.class);
        assertThat(movie, notNullValue());
        assertThat(movie.title(), not(emptyOrNullString()));
        assertThat(movie.year(), greaterThan(0));
        assertThat(movie.rating(), greaterThan(0f));
        assertThat(movie.votes(), greaterThan(0));
        assertThat(movie.genre(), not(emptyOrNullString()));
        assertThat(movie.director(), not(emptyOrNullString()));
        assertThat(movie.plot(), not(emptyOrNullString()));
        assertThat(movie.poster(), not(emptyOrNullString()));
    }
}
