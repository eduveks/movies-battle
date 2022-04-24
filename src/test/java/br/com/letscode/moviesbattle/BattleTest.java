package br.com.letscode.moviesbattle;

import br.com.letscode.moviesbattle.service.BattleMatchService;
import br.com.letscode.moviesbattle.service.BattleRankService;
import br.com.letscode.moviesbattle.util.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

public class BattleTest extends BaseTest {

    private void testMatchRoundMovie(BattleMatchService.MatchRoundMovie matchRoundMovie) {
        assertThat(matchRoundMovie.title(), not(emptyOrNullString()));
        assertThat(matchRoundMovie.year(), is(greaterThan(0)));
        assertThat(matchRoundMovie.genre(), not(emptyOrNullString()));
        assertThat(matchRoundMovie.director(), not(emptyOrNullString()));
        assertThat(matchRoundMovie.plot(), not(emptyOrNullString()));
        assertThat(matchRoundMovie.poster(), not(emptyOrNullString()));
    }

    private void testMatchRound(BattleMatchService.MatchRound matchRound) {
        assertThat(matchRound, notNullValue());
        assertThat(matchRound.first(), notNullValue());
        assertThat(matchRound.second(), notNullValue());
        testMatchRoundMovie(matchRound.first());
        testMatchRoundMovie(matchRound.second());
    }

    private void rank() {
        restLogged()
                .body(Map.of(
                        "page", 1
                ))
                .when()
                .post("/api/battle/rank")
                .then()
                .statusCode(is(200))
                .extract()
                .as(BattleRankService.Rank.class);
    }

    public void matchRounds(String username, String password, int rounds) {
        restLogin(username, password);

        restLogged()
                .when()
                .get("/api/battle/match/start")
                .then()
                .statusCode(is(204));

        for (var i = 0; i < rounds; i++) {
            testMatchRound(
                    restLogged()
                            .when()
                            .get("/api/battle/match/round")
                            .then()
                            .statusCode(is(200))
                            .extract().as(BattleMatchService.MatchRound.class)
            );
            var result = restLogged()
                    .body(Map.of(
                            "bid", Math.round(1 * Math.random()) + 1
                    ))
                    .when()
                    .post("/api/battle/match/round")
                    .then()
                    .statusCode(is(200))
                    .extract().as(BattleMatchService.MatchRoundResult.class);
            if (result.ended()) {
                rank();
                return;
            }
        }

        restLogged()
                .when()
                .get("/api/battle/match/end")
                .then()
                .statusCode(is(204));
        rank();
    }

    @Test
    public void match() {
        matchRounds("user1", "123", 5);
        matchRounds("user2", "123", 5);
        matchRounds("user3", "123", 10);
        matchRounds("user4", "123", 10);
        matchRounds("user1", "123", 5);
        matchRounds("user2", "123", 5);
    }
}
