package com.project.luvsick.repo;

import com.project.luvsick.model.Category;
import com.project.luvsick.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository  extends JpaRepository<Product, UUID> {
    Page<Product> findAllByCategory(Category category, Pageable pageable);
    List<Product> findTop5ByOrderByCreatedAtDesc();
}
