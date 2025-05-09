package com.project.luvsick.controller;

import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.mapper.OrderMapper;

import com.project.luvsick.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/createOrder")
    public ResponseEntity<OrderResponseDTO>createOrder(@RequestBody @Valid OrderDTO orderDTO){
        OrderResponseDTO orderResponseDTO=orderMapper.toDto(orderService.createNewOrder(orderDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }
}
