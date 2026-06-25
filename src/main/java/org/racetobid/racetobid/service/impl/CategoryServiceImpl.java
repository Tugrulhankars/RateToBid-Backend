package org.racetobid.racetobid.service.impl;

import org.racetobid.racetobid.dto.request.CreateCategoryRequest;
import org.racetobid.racetobid.entity.Category;
import org.racetobid.racetobid.repository.CategoryRepository;
import org.racetobid.racetobid.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private  final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String createCategory(CreateCategoryRequest request) {
        Category category=new Category();
        category.setName(request.getName());

        try {
            categoryRepository.save(category);
            return "Category created successfully";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<Category> getAllCategories() {

        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category=categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return category;
    }

    @Override
    public String updateCategory(Long id, CreateCategoryRequest request) {
        Category category=categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(request.getName());
        categoryRepository.save(category);
        return "";
    }

    @Override
    public String deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return "";
    }
}
