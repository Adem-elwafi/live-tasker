package tn.iset.dsi.livetasker.features.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    // Bonus: often useful for login logic
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}