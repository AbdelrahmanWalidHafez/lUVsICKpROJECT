package com.project.luvsick.mapper;
import com.project.luvsick.dto.AddProductRequestDTO;
import com.project.luvsick.dto.AddProductResponseDTO;
import com.project.luvsick.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final ProductSizesMapper productSizesMapper;
    public Product toProduct(AddProductRequestDTO addProductRequestDTO){
        Product product = Product
                .builder()
                .name(addProductRequestDTO.getName())
                .price(addProductRequestDTO.getPrice())
                .description(addProductRequestDTO.getDescription())
                .discount(addProductRequestDTO.getDiscount())
                .cost(addProductRequestDTO.getCost())
                .build();
        product.setSizes(addProductRequestDTO
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

    public AddProductResponseDTO toDTO(Product product) {
        return AddProductResponseDTO
                .builder()
                .id(product.getId())
                .description(product.getDescription())
                .name(product.getName())
                .cost(product.getCost())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .categoryResponseDTO(categoryMapper.toDto(product.getCategory()))
                .build();
    }
}
