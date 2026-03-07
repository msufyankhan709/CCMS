package com.campus.complaint.repository;

import com.campus.complaint.model.User;
import com.campus.complaint.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    Optional<User> findByVerificationToken(String verificationToken);
    
    long countByActiveTrue();
}
