package com.example.jtwsecurty.repository;

import com.example.jtwsecurty.entitys.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository  <UserEntity, Long> {

    // find an optional user by username
    Optional<UserEntity> findByUsername(String username);

    // find an optional user by id
    Optional<UserEntity> findById(Long id);

    // check if the username exist
    Boolean existsByUsername(String username);
}
