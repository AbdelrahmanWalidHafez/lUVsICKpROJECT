package com.project.luvsick.controller;
import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.mapper.OrderMapper;
import com.project.luvsick.model.OrderStatus;
import com.project.luvsick.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/createOrder")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        OrderResponseDTO orderResponseDTO = orderMapper.toDto(orderService.createNewOrder(orderDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }

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
            return ResponseEntity.ok(orderResponseDTOS);
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID id, @RequestParam OrderStatus orderStatus){
        orderService.updateStatus(id,orderStatus);
        return ResponseEntity.ok().build();
    }
}
