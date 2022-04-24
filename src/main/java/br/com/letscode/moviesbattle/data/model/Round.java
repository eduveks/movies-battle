package br.com.letscode.moviesbattle.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstMovieId;

    private float firstMovieScore;

    private String secondMovieId;

    private float secondMovieScore;

    private int userMovieBid;

    private boolean userWon;

    private Timestamp started;

    private Timestamp ended;

    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;
}
