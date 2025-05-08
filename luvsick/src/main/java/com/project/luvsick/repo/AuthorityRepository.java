package com.project.luvsick.repo;

import com.project.luvsick.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorityRepository  extends JpaRepository<Authority, UUID> {
    Optional<Authority> findByName(String name);
}
