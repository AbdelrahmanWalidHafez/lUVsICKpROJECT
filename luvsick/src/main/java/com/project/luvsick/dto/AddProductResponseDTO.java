package com.project.luvsick.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public class AddProductResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private BigDecimal cost;
    private CategoryResponseDTO categoryResponseDTO;
    private int discount ;
    private String description;

}
