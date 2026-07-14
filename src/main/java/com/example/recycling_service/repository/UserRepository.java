package com.example.recycling_service.repository;

import com.example.recycling_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Boolean existsByLogin(String login);
    Boolean existsByEmail(String email);
    //Boolean existsByUserId(String userId);
    User save(User user);

    @Query("SELECT u FROM User u WHERE u.login = :login")
    Optional<User> findByUsername(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByUserId(@Param("userId") UUID userId);
}