package br.com.letscode.moviesbattle.data.repository;

import br.com.letscode.moviesbattle.data.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> { }
