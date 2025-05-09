package com.project.luvsick.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class ProductResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private BigDecimal cost;
    private CategoryResponseDTO categoryResponseDTO;
    private int discount ;
    private String description;
    private List<ProductSizesResponse>productSizeDTOS;
}
