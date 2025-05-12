package com.project.luvsick.repo;

import com.project.luvsick.model.Order;
import com.project.luvsick.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository  extends JpaRepository<Order, UUID> {
    public Page<Order> findAllByStatus(OrderStatus OrderStatus, Pageable pageable);
}
