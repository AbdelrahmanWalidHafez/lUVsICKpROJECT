package com.project.luvsick.service;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ProductService {
     ProductResponseDTO addProduct(ProductDTO productDTO, MultipartFile multipartFile) throws IOException;

     void deleteProduct(UUID id);

     ProductResponseDTO editProduct(ProductDTO editProductRequestDTO, MultipartFile multipartFile,UUID id) throws IOException;

     List<ProductResponseDTO> getAllProducts(String categoryName, int pageNum, String sortDir, String sortField);

     List<ProductResponseDTO> getNewArrivals();
}
