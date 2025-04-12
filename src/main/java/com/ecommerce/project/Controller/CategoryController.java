package com.ecommerce.project.Controller;

import com.ecommerce.project.Payload.CategoryDTO;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.config.AppConstants;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name= "pageNumber", defaultValue= AppConstants.PAGE_NUMBER, required= false) Integer pageNumber,
            @RequestParam(name= "pageSize", required= false, defaultValue= AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name="sortBy", required= false, defaultValue= AppConstants.SORT_BY_CATEGORYID) String sortBy,
            @RequestParam(name="sortOrder", required= false, defaultValue= AppConstants.SORT_ORDER) String sortOrder
    ){
         CategoryResponse response= categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO categoryCreated= categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(categoryCreated, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable long categoryId){
        CategoryDTO response= categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId){
        CategoryDTO response= categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
