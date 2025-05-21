package com.project.luvsick.service;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;
/**
 * Service interface for managing product categories.
 * @author Abdelrahman Walid Hafez
 */
public interface CategoryService {

     /**
      * Adds a new category based on the provided request data.
      *
      * @param addCategoryRequestDTO the data transfer object containing category details to add
      * @return the added category as a response DTO
      */
     CategoryResponseDTO addCategory(AddCategoryRequestDTO addCategoryRequestDTO);

     /**
      * Deletes the category identified by the given UUID.
      *
      * @param id the UUID of the category to delete
      */
     void deleteCategory(UUID id);
     /**
      * Retrieves a list of all categories.
      *
      * @return a list of category response DTOs
      */
     List <CategoryResponseDTO> getCategories();

}
