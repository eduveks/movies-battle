package br.com.letscode.moviesbattle.data.repository;

import br.com.letscode.moviesbattle.data.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u ORDER BY u.score DESC")
    List<User> findRank(Pageable pageable);
}