package com.project.luvsick.service;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import com.project.luvsick.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    public ProductResponseDTO addProduct(ProductDTO productDTO, MultipartFile multipartFile) throws IOException;

    public void deleteProduct(UUID id);

    public ProductResponseDTO editProduct(ProductDTO editProductRequestDTO, MultipartFile multipartFile,UUID id) throws IOException;

    public List<ProductResponseDTO> getAllProducts(String categoryName, int pageNum, String sortDir, String sortField);

    public List<ProductResponseDTO> getNewArrivals();
}
