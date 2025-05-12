package com.project.luvsick.service.impl;
import com.project.luvsick.dto.ProductDTO;
import com.project.luvsick.dto.ProductResponseDTO;
import com.project.luvsick.mapper.ProductMapper;
import com.project.luvsick.mapper.ProductSizesMapper;
import com.project.luvsick.model.Category;
import com.project.luvsick.model.Product;
import com.project.luvsick.model.ProductSizes;
import com.project.luvsick.repo.CategoryRepository;
import com.project.luvsick.repo.ProductRepository;
import com.project.luvsick.service.EmailService;
import com.project.luvsick.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class productServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSizesMapper productSizesMapper;
    private final EmailService emailService;
    @Override
    @Transactional
    public ProductResponseDTO addProduct(ProductDTO productDTO, MultipartFile multipartFile) throws IOException {
        Product product=productMapper.toProduct(productDTO);
        Category category=categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(()->new EntityNotFoundException("Could not found a category with id"+ productDTO.getCategoryId()));
            product.setCategory(category);
            product.setImageName(multipartFile.getOriginalFilename());
            product.setImageType(multipartFile.getContentType());
            product.setImageData(multipartFile.getBytes());
            productRepository.save(product);
            log.info("new product added");
           emailService.sendNewArrivalEmail(product.getId());
            return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product=productRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Could not found a product with id "+id));
        productRepository.delete(product);
        log.info("product:{} is deleted",product.getName());
    }

    @Override
    @Transactional
    public ProductResponseDTO editProduct(ProductDTO dto, MultipartFile multipartFile,UUID id) throws IOException  {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a product with id " + id));
        product.setName(dto.getName());
        product.setCost(dto.getCost() != null ? dto.getCost() : product.getCost());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        if (dto.getSizes() != null) {
            Map<String, ProductSizes> existingSizesMap = product.getSizes().stream()
                .collect(Collectors.toMap(ProductSizes::getSize, size -> size));
            dto.getSizes().forEach(sizeDTO -> {
                if (existingSizesMap.containsKey(sizeDTO.getName())) {
                    ProductSizes existingSize = existingSizesMap.get(sizeDTO.getName());
                    existingSize.setQuantity(sizeDTO.getStock());
                } else {
                    ProductSizes newSize = productSizesMapper.toProductSizes(sizeDTO);
                    newSize.setProduct(product);
                    product.getSizes().add(newSize);
                }
            });
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            product.setImageType(multipartFile.getContentType());
            product.setImageData(multipartFile.getBytes());
            product.setImageName(multipartFile.getOriginalFilename());
        }
        Product savedProduct = productRepository.save(product);
        log.info("Product: {} is updated", product.getName());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public List<ProductResponseDTO> getAllProducts(String categoryName,int pageNum, String sortDir, String sortField) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of
                (pageNum - 1
                        , pageSize
                        , sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
        Page<Product> products;
        if (categoryName==null) {
            products=productRepository.findAll(pageable);
            return products
                    .getContent()
                    .stream()
                    .map(productMapper::toDTO).
                    collect(Collectors.toList());
        }
        Category category=categoryRepository
                .findByName(categoryName)
                .orElseThrow(()->new EntityNotFoundException("can not found a category with name:"+categoryName));
        products=productRepository.findAllByCategory(category,pageable);
        return products
                .getContent()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProductResponseDTO> getNewArrivals() {
        List<Product> products=productRepository.findTop5ByOrderByCreatedAtDesc();
        return products.stream().map(productMapper::toDTO).collect(Collectors.toList());
    }
}
