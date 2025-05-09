package com.project.luvsick.service;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    public CategoryResponseDTO addCategory(AddCategoryRequestDTO addCategoryRequestDTO);
    public void deleteCategory(UUID id);

   public List <CategoryResponseDTO> getCategories();

    public CategoryResponseDTO getCategory(UUID id);
}
