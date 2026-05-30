package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladislav.cost_analysis_nau.dto.CategoryForm;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public List<Category> getByType(boolean isIncome) {
        return categoryRepository.findByIsIncome(isIncome);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Transactional
    public Category create(CategoryForm form) {
        if (categoryRepository.existsByName(form.getName())) {
            throw new RuntimeException("Category with name '" + form.getName() + "' already exists");
        }
        Category category = new Category();
        category.setName(form.getName());
        category.setIncome(form.isIncome());
        log.info("Creating category '{}'", form.getName());
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, CategoryForm form) {
        Category category = getById(id);
        if (!category.getName().equals(form.getName()) && categoryRepository.existsByName(form.getName())) {
            throw new RuntimeException("Category with name '" + form.getName() + "' already exists");
        }
        category.setName(form.getName());
        category.setIncome(form.isIncome());
        log.info("Updating category {}", id);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        log.info("Deleting category '{}'", category.getName());
        categoryRepository.delete(category);
    }
}
