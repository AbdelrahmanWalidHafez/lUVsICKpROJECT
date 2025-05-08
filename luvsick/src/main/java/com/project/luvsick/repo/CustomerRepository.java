package com.project.luvsick.repo;

import com.project.luvsick.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository  extends JpaRepository<Customer, UUID> {
}
