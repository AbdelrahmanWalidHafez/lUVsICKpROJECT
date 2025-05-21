package com.project.luvsick.mapper;

import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @author Abdelrahman Walid Hafez
 */
@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ProductMapper productMapper;
    private final CustomerMapper customerMapper;
    /**
     * Converts an {@link Order} entity to an {@link OrderResponseDTO} data transfer object.
     *
     * @param order the {@link Order} entity to convert
     * @return an {@link OrderResponseDTO} representing the order, including customer info,
     *         order status, total price, product details, quantities, and creation timestamp
     */
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
