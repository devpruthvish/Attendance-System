package com.attendance.repository;

import com.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for User entity.
 * JpaRepository provides built-in: save, findById, findAll, delete, count, etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA auto-generates the SQL from the method name:
    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
