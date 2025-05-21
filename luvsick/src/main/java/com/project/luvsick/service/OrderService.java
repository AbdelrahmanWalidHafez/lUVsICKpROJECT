package com.project.luvsick.service;

import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.model.Order;
import com.project.luvsick.model.OrderStatus;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing orders.
 * @author Abdelrahman Walid Hafez
 */
public interface OrderService {
     /**
      * Creates a new order based on the provided OrderDTO.
      *
      * @param orderDTO the data transfer object containing order details
      * @return the created Order entity
      */
     Order createNewOrder(OrderDTO orderDTO) ;

     /**
      * Retrieves a paginated list of orders filtered by the given order status,
      * sorted according to the provided sorting direction and field.
      *
      * @param orderStatus the status to filter orders by (can be null for all statuses)
      * @param pageNum     the page number to retrieve (0-based)
      * @param sortDir     the sort direction, e.g., "asc" or "desc"
      * @param sortField   the field name to sort by
      * @return a list of OrderResponseDTO matching the criteria
      */
     List<OrderResponseDTO> getOrders(OrderStatus orderStatus, int pageNum, String sortDir, String sortField);
     /**
      * Updates the status of the order identified by the given UUID.
      *
      * @param id          the UUID of the order to update
      * @param orderStatus the new status to set on the order
      */
     void updateStatus(UUID id, OrderStatus orderStatus);
}
