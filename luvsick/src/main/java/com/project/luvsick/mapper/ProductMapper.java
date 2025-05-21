package com.project.luvsick.mapper;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import com.project.luvsick.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @author Abdelrahman Walid Hafez
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final ProductSizesMapper productSizesMapper;
    /**
     * Converts a {@link ProductDTO} to a {@link Product} entity.
     * <p>
     * Maps the basic fields and also converts and associates product sizes
     *  to the {@link com.project.luvsick.model.ProductSizes} entities.
     *
     * @param productDTO the data transfer object containing product data
     * @return the {@link Product} entity built from the DTO, including sizes properly linked
     */
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
    /**
     * Converts a {@link Product} entity to a {@link ProductResponseDTO}.
     * <p>
     * Maps product details, category information, and sizes to their respective DTOs.
     *
     * @param product the product entity to convert
     * @return a {@link ProductResponseDTO} representing the product and its details
     */
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
