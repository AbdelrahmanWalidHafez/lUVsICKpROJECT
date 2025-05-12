package com.project.luvsick.service;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
     CategoryResponseDTO addCategory(AddCategoryRequestDTO addCategoryRequestDTO);
     void deleteCategory(UUID id);
     List <CategoryResponseDTO> getCategories();

}
