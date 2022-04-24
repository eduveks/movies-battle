package br.com.letscode.moviesbattle.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp started;

    private Timestamp ended;

    private int roundsAmount;

    private int roundsWon;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @OrderBy("started")
    private Collection<Round> rounds = new ArrayList<>();
}
