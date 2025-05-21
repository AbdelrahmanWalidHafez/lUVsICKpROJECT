package com.project.luvsick.controller;
import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;
import com.project.luvsick.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * REST controller for managing product categories.
 * @author Abdelrahman Walid Hafez
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")

public class CategoryController {
    private final CategoryService categoryService;
    /**
     * Adds a new product category to the system.
     * <p>
     * This endpoint receives category details via a {@link AddCategoryRequestDTO},
     * validates the input, and delegates the creation process to the {@code categoryService}.
     * A successful creation returns the newly created category data.
     * </p>
     *
     * @param categoryDTO The category data to create, validated using Bean Validation.
     * @return A {@link ResponseEntity} containing the created {@link CategoryResponseDTO} and HTTP status {@code 201 Created}.
     */
    @PostMapping("/addCategory")
    public ResponseEntity<CategoryResponseDTO> addCategory(@RequestBody @Valid AddCategoryRequestDTO categoryDTO){
       CategoryResponseDTO responseDTO=categoryService.addCategory(categoryDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    /**
     * Deletes a category by its unique identifier.
     * <p>
     * This endpoint delegates the deletion of a category to the {@code categoryService}.
     * If the category with the given ID does not exist, the service layer is expected to handle that scenario,
     * potentially by throwing an exception.
     * </p>
     *
     * @param id The UUID of the category to be deleted.
     * @return A {@link ResponseEntity} with HTTP status {@code 204 No Content} if deletion is successful.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Retrieves a list of all available product categories.
     * <p>
     * This endpoint returns a list of {@link CategoryResponseDTO} representing all categories
     * currently stored in the system. The response is cached for 15 minutes to improve performance.
     * </p>
     *
     * @return A {@link ResponseEntity} containing the list of categories with HTTP status {@code 200 OK}.
     */
    @GetMapping("/getCategories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(){
       return ResponseEntity.status(HttpStatus.OK)
               .cacheControl(CacheControl.maxAge(15,TimeUnit.MINUTES))
               .body(categoryService.getCategories());
    }
}
