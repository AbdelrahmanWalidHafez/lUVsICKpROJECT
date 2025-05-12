package com.project.luvsick.repo;

import com.project.luvsick.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository  extends JpaRepository<Customer, UUID> {
    @Query("SELECT c.email FROM Customer c")
    public List<String> findAllEmails();
    @Query("SELECT c.email FROM Customer c")
    public List<String> findAllEmails(String email);
    Optional<Customer> findByEmail(String email);
}
