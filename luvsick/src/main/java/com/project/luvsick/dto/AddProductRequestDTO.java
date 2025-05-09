package com.project.luvsick.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price must be less than 1,000,000")
    private BigDecimal price;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.01", message = "Cost must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Cost must be less than 1,000,000")
    private BigDecimal cost;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotEmpty(message = "At least one size is required")
    private List<ProductSizeDTO> sizes;

    @Min(value = 0, message = "Discount must be at least 0")
    @Max(value = 100, message = "Discount cannot exceed 100")
    private int discount;
}
