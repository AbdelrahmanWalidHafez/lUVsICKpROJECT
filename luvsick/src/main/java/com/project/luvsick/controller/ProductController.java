package com.project.luvsick.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.luvsick.dto.AddProductRequestDTO;
import com.project.luvsick.dto.AddProductResponseDTO;
import com.project.luvsick.repo.ProductRepository;
import com.project.luvsick.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;
    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> addProduct(@RequestPart("addProductRequestDTO") @Valid String addProductRequestDTO, @RequestPart("multipartFile") MultipartFile multipartFile){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AddProductRequestDTO dto = objectMapper.readValue(addProductRequestDTO, AddProductRequestDTO.class);
             AddProductResponseDTO addProductResponseDTO=productService.addProduct(dto,multipartFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(addProductResponseDTO);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @DeleteMapping(path = {"/id"})
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
