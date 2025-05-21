package com.project.luvsick.service;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing products.
 * @author Abdelrahman Walid Hafez
 */
public interface ProductService {
     /**
      * Adds a new product with an associated image file.
      *
      * @param productDTO     the product data transfer object containing product details
      * @param multipartFile  the image file for the product
      * @return the added product as a ProductResponseDTO
      * @throws IOException if an error occurs during file processing
      */
     ProductResponseDTO addProduct(ProductDTO productDTO, MultipartFile multipartFile) throws IOException;
     /**
      * Deletes a product by its unique identifier.
      *
      * @param id the UUID of the product to delete
      */
     void deleteProduct(UUID id);
     /**
      * Edits an existing product identified by its UUID, including updating its image.
      *
      * @param editProductRequestDTO the product data transfer object containing updated product details
      * @param multipartFile         the new image file for the product
      * @param id                    the UUID of the product to edit
      * @return the updated product as a ProductResponseDTO
      * @throws IOException if an error occurs during file processing
      */
     ProductResponseDTO editProduct(ProductDTO editProductRequestDTO, MultipartFile multipartFile,UUID id) throws IOException;
     /**
      * Retrieves a paginated list of products filtered by category name, sorted according to the given direction and field.
      *
      * @param categoryName the name of the category to filter products by (can be null or empty for all categories)
      * @param pageNum      the page number to retrieve (0-based)
      * @param sortDir      the sort direction, e.g., "asc" or "desc"
      * @param sortField    the field name to sort by
      * @return a list of ProductResponseDTO matching the criteria
      */
     List<ProductResponseDTO> getAllProducts(String categoryName, int pageNum, String sortDir, String sortField);
     /**
      * Retrieves a list of newly arrived products.
      *
      * @return a list of ProductResponseDTO representing new arrivals
      */
     List<ProductResponseDTO> getNewArrivals();
}
