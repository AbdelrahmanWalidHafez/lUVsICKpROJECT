package com.project.luvsick.controller;
import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.mapper.OrderMapper;
import com.project.luvsick.model.OrderStatus;
import com.project.luvsick.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * REST controller for managing orders.
 * <p>
 * Provides endpoints to create new orders, retrieve orders with filtering, sorting, and pagination,
 * and update the status of existing orders.
 * </p>
 * @author Abdelrahman Walid Hafez
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    /**
     * Creates a new order based on the provided OrderDTO.
     *
     * @param orderDTO the data transfer object containing the order details
     *                 to be created; must be valid according to validation constraints
     * @return ResponseEntity containing the created OrderResponseDTO and HTTP status 201 (Created)
     */
    @PostMapping("/createOrder")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        OrderResponseDTO orderResponseDTO = orderMapper.toDto(orderService.createNewOrder(orderDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }
    /**
     * Retrieves a list of orders optionally filtered by order status, with pagination and sorting.
     *
     * @param orderStatus the status to filter orders by (optional)
     * @param pageNum     the page number to retrieve, starting from 1 (default is 1)
     * @param sortDir     the direction of sorting, either "asc" or "desc" (default is "desc")
     * @param sortField   the field to sort by; allowed values are "totalPrice" and "createdAt" (default is "createdAt")
     * @return ResponseEntity containing a list of OrderResponseDTO objects;
     *         returns HTTP 400 with an empty list if an invalid sortField is provided;
     *         otherwise returns HTTP 200 with the list of orders and cache control header
     */
    @GetMapping("/getOrders")

    public ResponseEntity<List<OrderResponseDTO>> getOrders(@RequestParam(required = false) OrderStatus orderStatus,
                                                            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                            @RequestParam(value = "sortField", defaultValue = "createdAt") String sortField) {
        List<String> allowedSortFields = List.of("totalPrice", "createdAt");
        if (!allowedSortFields.contains(sortField)) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.emptyList());
        }
            List<OrderResponseDTO> orderResponseDTOS = orderService.getOrders(orderStatus, pageNum, sortDir, sortField);
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.SECONDS))
                    .body(orderResponseDTOS);
    }
    /**
     * Updates the status of an existing order identified by the given UUID.
     *
     * @param id          the UUID of the order to update
     * @param orderStatus the new status to set for the order
     * @return ResponseEntity with HTTP 200 OK status if the update was successful
     */
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID id, @RequestParam OrderStatus orderStatus){
        orderService.updateStatus(id,orderStatus);
        return ResponseEntity.ok().build();
    }
}
