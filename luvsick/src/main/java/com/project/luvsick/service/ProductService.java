package com.project.luvsick.service;
import com.project.luvsick.dto.AddProductRequestDTO;
import com.project.luvsick.dto.AddProductResponseDTO;
import com.project.luvsick.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProductService {
    public AddProductResponseDTO addProduct(AddProductRequestDTO addProductRequestDTO, MultipartFile multipartFile) throws IOException;

    public void deleteProduct(UUID id);
}
