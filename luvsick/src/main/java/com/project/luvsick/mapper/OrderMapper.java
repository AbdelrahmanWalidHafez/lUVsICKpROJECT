package com.project.luvsick.mapper;
import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ProductMapper productMapper;
    private final CustomerMapper customerMapper;
    public OrderResponseDTO toDto(Order order){
        return OrderResponseDTO
                .builder()
                .customerDTO(customerMapper.toDto(order.getCustomer()))
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .id(order.getId())
                .itemPerQuantity(order.getItemPerQuantity())
                .productResponseDTOS(order.getProducts().stream().map(productMapper::toDTO).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }
}
