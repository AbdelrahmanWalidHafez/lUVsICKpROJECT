package com.project.luvsick.dto;

import com.project.luvsick.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
public class OrderResponseDTO {
    private UUID id;
    private CustomerDTO customerDTO;
    private List<ProductResponseDTO> productResponseDTOS;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private Map<UUID,Integer>itemPerQuantity;
}
