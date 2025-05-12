package com.project.luvsick.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import com.project.luvsick.mapper.ProductMapper;
import com.project.luvsick.model.Product;
import com.project.luvsick.repo.ProductRepository;
import com.project.luvsick.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;
    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(@RequestPart("addProductRequestDTO") @Valid String addProductRequestDTO, @RequestPart("multipartFile") MultipartFile multipartFile){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO dto = objectMapper.readValue(addProductRequestDTO, ProductDTO.class);
             ProductResponseDTO productResponseDTO =productService.addProduct(dto,multipartFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<?>editProduct(@RequestPart("editProductRequestDTO") @Valid String editProductRequestDTO
            , @RequestPart("multipartFile") MultipartFile multipartFile
            ,@PathVariable UUID id){
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            ProductDTO dto=objectMapper.readValue(editProductRequestDTO,ProductDTO.class);
            ProductResponseDTO productResponseDTO=productService.editProduct(dto,multipartFile,id);
            return ResponseEntity.ok(productResponseDTO);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping(value = "/allProducts")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String categoryName,
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "sortField", defaultValue = "createdAt") String sortField){
        List<String> allowedSortFields = List.of("price", "createdAt");
        if (!allowedSortFields.contains(sortField)) {
            return ResponseEntity
                    .badRequest()
                    .body(Collections.emptyList());
        }
        List<ProductResponseDTO> productResponseDTOS = productService.getAllProducts(categoryName,pageNum, sortDir, sortField);
        return ResponseEntity.ok(productResponseDTOS);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageType()))
                .body(product.getImageData());
    }
    @GetMapping("/newArrivals")
    public ResponseEntity<List<ProductResponseDTO>>getNewArrivals(){
        List<ProductResponseDTO> productResponseDTOS=productService.getNewArrivals();
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDTOS);
    }
}
