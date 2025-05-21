package com.project.luvsick.mapper;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;
import com.project.luvsick.model.Category;
import org.springframework.stereotype.Component;

/**
 * @author Abdelrahman Walid Hafez
 */
@Component
public class CategoryMapper {
    /**
     * Converts an {@link AddCategoryRequestDTO} to a {@link Category} entity.
     *
     * @param categoryRequestDTO the DTO containing data to create a Category
     * @return a new {@link Category} entity with fields set from the DTO
     */
    public Category toCategory(AddCategoryRequestDTO categoryRequestDTO) {
        return  Category
                .builder()
                .name(categoryRequestDTO.getName())
                .build();
    }
    /**
     * Converts a {@link Category} entity to a {@link CategoryResponseDTO}.
     *
     * @param category the {@link Category} entity to convert
     * @return a {@link CategoryResponseDTO} populated with data from the entity
     */
    public CategoryResponseDTO toDto(Category category) {
        return  CategoryResponseDTO
                .builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
