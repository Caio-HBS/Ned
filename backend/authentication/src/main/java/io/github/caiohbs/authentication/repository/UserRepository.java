package io.github.caiohbs.authentication.repository;

import io.github.caiohbs.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    Optional<User> findByUserGroup(String userGroup);
    Optional<User> findByUniqueLocalIdentification(String uniqueLocalIdentification);
}

