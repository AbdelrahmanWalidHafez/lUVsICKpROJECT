package com.project.luvsick.service;

import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.model.Order;
import com.project.luvsick.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
     Order createNewOrder(OrderDTO orderDTO) ;

     List<OrderResponseDTO> getOrders(OrderStatus orderStatus, int pageNum, String sortDir, String sortField);

     void updateStatus(UUID id, OrderStatus orderStatus);
}
