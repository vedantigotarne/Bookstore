package com.example.bookstore.controller;

import com.example.bookstore.dao.CategoryDAO;
import com.example.bookstore.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryDAO categoryDAO;

    @ModelAttribute("categories")
    public List<Category> addCategoriesToModel() {
        return categoryDAO.getAllCategories();
    }
}