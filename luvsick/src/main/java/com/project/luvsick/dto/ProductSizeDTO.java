package com.project.luvsick.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeDTO {
    @NotBlank
    private UUID id;
    @NotBlank(message = "Size name is required")
    private String name;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 1, message = "Stock quantity cannot be negative")
    private Integer stock;
} 