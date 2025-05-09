package com.project.luvsick.service;
import com.project.luvsick.dto.AddProductRequestDTO;
import com.project.luvsick.dto.AddProductResponseDTO;
import com.project.luvsick.mapper.ProductMapper;
import com.project.luvsick.model.Category;
import com.project.luvsick.model.Product;
import com.project.luvsick.model.ProductSizes;
import com.project.luvsick.repo.CategoryRepository;
import com.project.luvsick.repo.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class productServiceImpl implements ProductService{
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public AddProductResponseDTO addProduct(AddProductRequestDTO addProductRequestDTO, MultipartFile multipartFile) throws IOException {
        Product product=productMapper.toProduct(addProductRequestDTO);
        Category category=categoryRepository
                .findById(addProductRequestDTO.getCategoryId())
                .orElseThrow(()->new EntityNotFoundException("Could not found a category with id"+addProductRequestDTO.getCategoryId()));
            product.setCategory(category);
            product.setImageName(multipartFile.getOriginalFilename());
            product.setImageType(multipartFile.getContentType());
            product.setImageData(multipartFile.getBytes());
            productRepository.save(product);
            return productMapper.toDTO(product);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product=productRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Could not found a product with id "+id));
        productRepository.delete(product);
    }
}
