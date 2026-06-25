package org.racetobid.racetobid.controller;

import org.racetobid.racetobid.dto.request.CreateCategoryRequest;
import org.racetobid.racetobid.entity.Category;
import org.racetobid.racetobid.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody CreateCategoryRequest request){
        String response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/getCategoryById/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id){
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody CreateCategoryRequest request){
        String response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        String response = categoryService.deleteCategory(id);
        return ResponseEntity.ok(response);
    }

}
