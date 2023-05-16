package com.example.jtwsecurty.repository;

import com.example.jtwsecurty.entitys.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository <Roles, Long> {

    // find an optional roles by name
    Optional<Roles> findByName(String name);

    // find an optional roles by id
    Optional<Roles> findById(Long id);
}
