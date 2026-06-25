package org.racetobid.racetobid.service;

import org.racetobid.racetobid.dto.request.CreateCategoryRequest;
import org.racetobid.racetobid.entity.Category;

import java.util.List;

public interface CategoryService {

    String createCategory(CreateCategoryRequest request);
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    String updateCategory(Long id, CreateCategoryRequest request);
    String deleteCategory(Long id);

}
