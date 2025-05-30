package com.project.luvsick.service.impl;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;
import com.project.luvsick.mapper.CategoryMapper;
import com.project.luvsick.model.Category;
import com.project.luvsick.repo.CategoryRepository;
import com.project.luvsick.service.CategoryService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public CategoryResponseDTO addCategory(AddCategoryRequestDTO addCategoryRequestDTO) {
        if (categoryRepository.findByName(addCategoryRequestDTO.getName()).isPresent()){
            throw new EntityExistsException("Resource already exists");
        }
        Category category=categoryMapper.toCategory(addCategoryRequestDTO);
         categoryRepository.save(category);
         log.info("new category is created");
         return  categoryMapper.toDto(category);
    }
    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category= categoryRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Did not found Category with id" + id));
        categoryRepository.delete(category);
        log.info("category:{} is deleted",category.getName());
    }
    @Override
    public List<CategoryResponseDTO> getCategories() {
        List<Category> categories=categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }
}
