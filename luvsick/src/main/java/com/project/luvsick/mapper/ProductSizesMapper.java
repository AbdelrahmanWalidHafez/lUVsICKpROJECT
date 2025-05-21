package com.project.luvsick.mapper;

import com.project.luvsick.dto.ProductSizeDTO;
import com.project.luvsick.dto.ProductSizesResponse;
import com.project.luvsick.model.ProductSizes;
import org.springframework.stereotype.Component;

/**
 * @author Abdelrahman Walid Hafez
 */
@Component
public class ProductSizesMapper {
    /**
     * Converts a {@link ProductSizeDTO} to a {@link ProductSizes} entity.
     *
     * @param productSizeDTO the DTO containing product size data
     * @return a {@link ProductSizes} entity built from the DTO
     */
    public  ProductSizes toProductSizes(ProductSizeDTO productSizeDTO) {
        return  ProductSizes
                .builder()
                .quantity(productSizeDTO.getStock())
                .size(productSizeDTO.getName())
                .build();
    }

    /**
     * Converts a {@link ProductSizes} entity to a {@link ProductSizesResponse} DTO.
     *
     * @param productSizes the entity to convert
     * @return a {@link ProductSizesResponse} DTO containing size and quantity information
     */
    public ProductSizesResponse toDto(ProductSizes productSizes){
        return ProductSizesResponse
                .builder()
                .id(productSizes.getId())
                .size(productSizes.getSize())
                .quantity(productSizes.getQuantity())
                .build();
    }
}
