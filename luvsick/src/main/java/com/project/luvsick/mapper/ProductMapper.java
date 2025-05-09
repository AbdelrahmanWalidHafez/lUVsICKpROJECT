package com.project.luvsick.mapper;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import com.project.luvsick.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final ProductSizesMapper productSizesMapper;
    public Product toProduct(ProductDTO productDTO){
        Product product = Product
                .builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .discount(productDTO.getDiscount())
                .cost(productDTO.getCost())
                .build();
        product.setSizes(productDTO
                .getSizes()
                .stream()
                .map(sizeDTO -> {
                    var size = productSizesMapper.toProductSizes(sizeDTO);
                    size.setProduct(product);
                    return size;
                })
                .collect(Collectors.toList()));

        return product;
    }

    public ProductResponseDTO toDTO(Product product) {
        return ProductResponseDTO
                .builder()
                .id(product.getId())
                .description(product.getDescription())
                .name(product.getName())
                .cost(product.getCost())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .categoryResponseDTO(categoryMapper.toDto(product.getCategory()))
                .productSizeDTOS(product.getSizes().stream().map(productSizesMapper::toDto).collect(Collectors.toList()))
                .build();
    }
}
