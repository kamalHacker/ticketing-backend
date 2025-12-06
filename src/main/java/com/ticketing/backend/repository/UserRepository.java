package com.ticketing.backend.repository;

import com.ticketing.backend.model.User;
import com.ticketing.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findFirstByRole(Role role);
    List<User> findByRole(Role role);
}
