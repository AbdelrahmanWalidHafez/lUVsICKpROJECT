package com.project.luvsick.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import com.project.luvsick.model.Product;
import com.project.luvsick.repo.ProductRepository;
import com.project.luvsick.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for managing products.
 * <p>
 * Provides endpoints to add, edit, delete, and retrieve products including
 * image retrieval and filtering by category, pagination, and sorting.
 * </p>
 * @author Abdelrahman Walid Hafez
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductService productService;

    /**
     * Adds a new product with the associated image file.
     * <p>
     * Expects a multipart request with the product details as a JSON string and an image file.
     * </p>
     *
     * @param addProductRequestDTO the product details as a JSON string, validated
     * @param multipartFile        the image file to be uploaded
     * @return ResponseEntity containing the created ProductResponseDTO with HTTP status 201 (Created),
     * or HTTP 500 if there's an error processing the request
     */
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
    /**
     * Deletes a product by its unique identifier.
     *
     * @param id the UUID of the product to delete
     * @return ResponseEntity with HTTP 204 No Content status upon successful deletion
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Edits an existing product identified by its UUID.
     * <p>
     * Accepts multipart request containing the updated product details as a JSON string
     * and a new image file.
     * </p>
     *
     * @param editProductRequestDTO the updated product details as a JSON string, validated
     * @param multipartFile         the new image file
     * @param id                    the UUID of the product to edit
     * @return ResponseEntity containing the updated ProductResponseDTO with HTTP 200 OK status,
     * or HTTP 500 if there's an error processing the request
     */
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
    /**
     * Retrieves a list of all products optionally filtered by category, with pagination and sorting.
     *
     * @param categoryName the category name to filter products by (optional)
     * @param pageNum      the page number to retrieve, starting from 1 (default is 1)
     * @param sortDir      the sorting direction ("asc" or "desc"), default is "desc"
     * @param sortField    the field to sort by; allowed values are "price" and "createdAt" (default is "createdAt")
     * @return ResponseEntity containing a list of ProductResponseDTO objects;
     *         returns HTTP 400 with an empty list if an invalid sortField is provided;
     *         otherwise returns HTTP 200 with the list and cache control headers
     */
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
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.SECONDS))
                .body(productResponseDTOS);
    }
    /**
     * Retrieves the image data for a product identified by its UUID.
     *
     * @param id the UUID of the product whose image is requested
     * @return ResponseEntity containing the image bytes with correct media type and cache headers;
     *         throws EntityNotFoundException if product is not found
     */
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageType()))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.SECONDS))
                .body(product.getImageData());
    }
    /**
     * Retrieves a list of newly arrived products.
     *
     * @return ResponseEntity containing a list of ProductResponseDTO for new arrivals with HTTP 200 OK status
     */
    @GetMapping("/newArrivals")
    public ResponseEntity<List<ProductResponseDTO>>getNewArrivals(){
        List<ProductResponseDTO> productResponseDTOS=productService.getNewArrivals();
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDTOS);
    }
}
