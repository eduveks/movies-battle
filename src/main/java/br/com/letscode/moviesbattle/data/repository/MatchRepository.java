package br.com.letscode.moviesbattle.data.repository;

import br.com.letscode.moviesbattle.data.model.Match;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.rounds r JOIN FETCH m.user u WHERE u.id = :userId ORDER BY m.started DESC")
    List<Match> findLatestByUser(@Param("userId") long userId, Pageable pageable);
}
