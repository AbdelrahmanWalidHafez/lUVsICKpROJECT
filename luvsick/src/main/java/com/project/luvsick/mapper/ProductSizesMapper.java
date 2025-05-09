package com.project.luvsick.mapper;

import com.project.luvsick.dto.ProductSizeDTO;
import com.project.luvsick.model.ProductSizes;
import org.springframework.stereotype.Component;

@Component
public class ProductSizesMapper {
    public  ProductSizes toProductSizes(ProductSizeDTO productSizeDTO) {
        return  ProductSizes
                .builder()
                .quantity(productSizeDTO.getStock())
                .size(productSizeDTO.getName())
                .build();
    }
}
