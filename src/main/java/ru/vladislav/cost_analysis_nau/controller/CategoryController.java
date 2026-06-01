package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladislav.cost_analysis_nau.dto.CategoryForm;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.service.CategoryService;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("incomeCategories", categoryService.getByType(true));
        model.addAttribute("expenseCategories", categoryService.getByType(false));
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new CategoryForm());
        return "categories/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute CategoryForm form, Model model) {
        try {
            categoryService.create(form);
            return "redirect:/categories";
        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            return "categories/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id);
        CategoryForm form = new CategoryForm();
        form.setName(category.getName());
        form.setIncome(category.isIncome());
        model.addAttribute("form", form);
        model.addAttribute("categoryId", id);
        return "categories/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute CategoryForm form, Model model) {
        try {
            categoryService.update(id, form);
            return "redirect:/categories";
        } catch (Exception e) {
            log.error("Error updating category {}: {}", id, e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("categoryId", id);
            return "categories/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
