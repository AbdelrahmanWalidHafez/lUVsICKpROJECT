package com.project.luvsick.repo;

import com.project.luvsick.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    public Optional<Category> findByName(String name);
}
