package com.project.luvsick.repo;

import com.project.luvsick.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends JpaRepository<UserRepository, UUID> {
    public Optional<User> findByEmail(String email);
}
