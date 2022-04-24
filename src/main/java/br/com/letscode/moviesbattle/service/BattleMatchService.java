package br.com.letscode.moviesbattle.service;

import br.com.letscode.moviesbattle.data.model.Match;
import br.com.letscode.moviesbattle.data.model.Round;
import br.com.letscode.moviesbattle.data.model.User;
import br.com.letscode.moviesbattle.data.repository.MatchRepository;
import br.com.letscode.moviesbattle.data.repository.RoundRepository;
import br.com.letscode.moviesbattle.data.repository.UserRepository;
import br.com.letscode.moviesbattle.service.exception.*;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BattleMatchService {
    private final OMDbAPIService omdbAPIService;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final RoundRepository roundRepository;

    /**
     * Find in the database the latest match for a user.
     * @param user Use this user to find your last match.
     * @return The last match of the user.
     */
    private Match getLatestMatch(User user) {
        var matches = matchRepository.findLatestByUser(user.getId(), PageRequest.ofSize(1));
        return matches.size() > 0 ? matches.get(0) : null;
    }

    /**
     * Loads one movie never used in the match, based on a list of movies IDs already used in the match.
     * @param usedMoviesIds The movie with ID listed here is forbidden.
     * @return The movie that is not listed and then never used.
     */
    private OMDbAPIService.Movie safeMovie(List<String> usedMoviesIds) throws InterruptedException {
        final OMDbAPIService.Movie tempMovie = omdbAPIService.next();
        if (!usedMoviesIds.contains(tempMovie.id())) {
            usedMoviesIds.add(tempMovie.id());
            return tempMovie;
        }
        return safeMovie(usedMoviesIds);
    }

    public void startMatch(User user) throws BattleMatchAlreadyStartedException {
        var existingMatch = getLatestMatch(user);
        if (existingMatch == null || existingMatch.getEnded() != null) {
            var match = new Match(null, Timestamp.from(Instant.now()), null, 0, 0, user, new ArrayList<>());
            matchRepository.save(match);
        } else {
            throw new BattleMatchAlreadyStartedException();
        }
    }

    public void endMatch(User user) throws BattleMatchAlreadyEndedException, BattleMatchNotStartedException, BattleMatchRoundNotSavedYetException {
        var match = getLatestMatch(user);
        if (match == null) {
            throw new BattleMatchNotStartedException();
        }
        if (match.getEnded() == null) {
            var rounds = match.getRounds();
            var latestRound = rounds.stream().sorted((a, b) -> -1).findFirst().orElse(null);
            if (latestRound != null && latestRound.getEnded() == null) {
                throw new BattleMatchRoundNotSavedYetException();
            }
            match.setEnded(Timestamp.from(Instant.now()));
            matchRepository.save(match);
        } else {
            throw new BattleMatchAlreadyEndedException();
        }
    }

    public MatchRound newRound(User user) throws InterruptedException, BattleMatchRoundNotSavedYetException, BattleMatchAlreadyEndedException {
        var match = getLatestMatch(user);
        if (match != null && match.getEnded() == null) {
            var rounds = match.getRounds();
            var existingRound = rounds.stream().sorted((a, b) -> -1).findFirst().orElse(null);
            if (existingRound == null || existingRound.getEnded() != null) {
                var usedMoviesIds = rounds.stream()
                        .map(r -> List.of(r.getFirstMovieId(), r.getSecondMovieId()))
                        .flatMap(Collection::stream).collect(Collectors.toList());
                OMDbAPIService.Movie firstMovie = safeMovie(usedMoviesIds);
                OMDbAPIService.Movie secondMovie = safeMovie(usedMoviesIds);
                var round = new Round(
                        null,
                        firstMovie.id(),
                        firstMovie.votes() * firstMovie.rating(),
                        secondMovie.id(),
                        secondMovie.votes() * secondMovie.rating(),
                        0,
                        false,
                        Timestamp.from(Instant.now()),
                        null,
                        match
                );
                roundRepository.save(round);
                match.getRounds().add(round);
                log.info(String.format("""
                        \n
                        \t%s ROUND %d - %s
                        
                        \t%s :: %.02f :: %s
                        \t%s
                        \t%s :: %.02f :: %s
                        \n""", EmojiParser.parseToUnicode(":game_die:"), rounds.size(), user.getUsername(),
                        firstMovie.id(), round.getFirstMovieScore(), firstMovie.title(),
                        EmojiParser.parseToUnicode(":heavy_multiplication_x:"),
                        secondMovie.id(), round.getSecondMovieScore(), secondMovie.title()
                ));
                return new MatchRound(
                        MatchRoundMovie.loadFrom(firstMovie),
                        MatchRoundMovie.loadFrom(secondMovie)
                );
            }
            throw new BattleMatchRoundNotSavedYetException();
        }
        throw new BattleMatchAlreadyEndedException();
    }

    public MatchRoundResult saveRound(User user, int bid) throws BattleMatchAlreadyEndedException, BattleMatchRoundNotStartedException, BattleMatchRoundInvalidBidException, BattleMatchNotStartedException, BattleMatchRoundNotSavedYetException {
        if (bid < 1 || bid > 2) {
            throw new BattleMatchRoundInvalidBidException(String.format("The bid value \"%d\" is invalid.", bid));
        }
        var match = getLatestMatch(user);
        if (match != null && match.getEnded() == null) {
            var rounds = match.getRounds();
            var round = rounds.stream().sorted((a, b) -> -1).findFirst().get();  //getLatestRound(match);
            if (round.getEnded() == null) {
                round.setUserWon(
                        (bid == 1 && round.getFirstMovieScore() >= round.getSecondMovieScore())
                                || (bid == 2 && round.getFirstMovieScore() <= round.getSecondMovieScore())
                );
                round.setUserMovieBid(bid);
                round.setEnded(Timestamp.from(Instant.now()));
                roundRepository.save(round);
                if (round.isUserWon()) {
                    match.setRoundsWon(match.getRoundsWon() + 1);
                    user.setRoundsWon(user.getRoundsWon() + 1);
                }
                match.setRoundsAmount(match.getRoundsAmount() + 1);
                matchRepository.save(match);
                user.setRoundsAmount(user.getRoundsAmount() + 1);
                user.setScore(
                        (int)Math.round(
                                (double)user.getRoundsAmount() * (
                                        ((double)user.getRoundsWon() / (double)user.getRoundsAmount())
                                                * 100.0d
                                )
                        )
                );
                userRepository.save(user);
                var matchEnded = rounds.stream().sorted((a, b) -> -1)
                        .limit(3)
                        .filter(r -> !r.isUserWon())
                        .count() == 3;
                if (matchEnded) {
                    endMatch(user);
                }
                log.info(String.format("""
                        \n
                        \t%s %s was %s with the movie %d.
                        \n""", round.isUserWon() ? EmojiParser.parseToUnicode(":trophy:") : EmojiParser.parseToUnicode(":x:"),
                        user.getUsername(), round.isUserWon() ? "victorious" : "defeated", bid
                ));
                return new MatchRoundResult(
                        round.getEnded().getTime() - round.getStarted().getTime(),
                        round.isUserWon(),
                        matchEnded
                );
            }
            throw new BattleMatchRoundNotStartedException();
        }
        throw new BattleMatchAlreadyEndedException();
    }

    public record MatchRound(MatchRoundMovie first, MatchRoundMovie second) {
    }

    public record MatchRoundMovie(String title, int year, String genre, String director, String plot, String poster) {
        public static MatchRoundMovie loadFrom(OMDbAPIService.Movie omdbMovie) {
            return new MatchRoundMovie(
                    omdbMovie.title(),
                    omdbMovie.year(),
                    omdbMovie.genre(),
                    omdbMovie.director(),
                    omdbMovie.plot(),
                    omdbMovie.poster()
            );
        }
    }

    public record MatchRoundResult(long time, boolean result, boolean ended) {
    }
}
