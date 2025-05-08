package com.project.luvsick.mappers;

import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.model.Product;
import java.util.Arrays;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-29T03:11:52+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO.ProductDTOBuilder productDTO = ProductDTO.builder();

        productDTO.id( product.getId() );
        productDTO.name( product.getName() );
        productDTO.description( product.getDescription() );
        productDTO.price( product.getPrice() );
        byte[] photo = product.getPhoto();
        if ( photo != null ) {
            productDTO.photo( Arrays.copyOf( photo, photo.length ) );
        }

        return productDTO.build();
    }
}
