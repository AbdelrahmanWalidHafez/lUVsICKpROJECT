package com.project.luvsick.mapper;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;
import com.project.luvsick.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toCategory(AddCategoryRequestDTO categoryRequestDTO) {
        return  Category
                .builder()
                .name(categoryRequestDTO.getName())
                .build();
    }
    public CategoryResponseDTO toDto(Category category) {
        return  CategoryResponseDTO
                .builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
