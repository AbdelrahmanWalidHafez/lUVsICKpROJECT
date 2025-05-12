package com.project.luvsick.controller;

import com.project.luvsick.dto.AddCategoryRequestDTO;
import com.project.luvsick.dto.CategoryResponseDTO;
import com.project.luvsick.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/addCategory")
    public ResponseEntity<CategoryResponseDTO> addCategory(@RequestBody @Valid AddCategoryRequestDTO categoryDTO){
       CategoryResponseDTO responseDTO=categoryService.addCategory(categoryDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/getCategories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(){
       return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories());
    }
}
