package com.project.luvsick.repo;

import com.project.luvsick.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository  extends JpaRepository<Product, UUID> {
}
