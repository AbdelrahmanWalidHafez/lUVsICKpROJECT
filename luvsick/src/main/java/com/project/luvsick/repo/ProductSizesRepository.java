package com.project.luvsick.repo;

import com.project.luvsick.model.ProductSizes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductSizesRepository  extends JpaRepository<ProductSizes, UUID> {
}
