package com.example.bookstore.controller;

import com.example.bookstore.dao.BookDAO;
//import com.example.bookstore.dao.CategoryDAO;
import com.example.bookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookListController {

    @Autowired
    private BookDAO bookDAO;

    @GetMapping("")
    public String showBooks(@RequestParam(value = "category", required = false) String categoryParam,
                           @RequestParam(value = "minPrice", required = false) String minPriceParam,
                           @RequestParam(value = "maxPrice", required = false) String maxPriceParam,
                           Model model) {
        List<Book> books = bookDAO.getAllBooks();
        if (categoryParam != null && !categoryParam.isEmpty()) {
            try {
                int categoryId = Integer.parseInt(categoryParam);
                books = bookDAO.getBooksByCategory(categoryId);
                model.addAttribute("selectedCategory", categoryId);
            } catch (NumberFormatException e) {
                // Invalid category ID, ignore filter
            }
        }
        if (minPriceParam != null && maxPriceParam != null && !minPriceParam.isEmpty() && !maxPriceParam.isEmpty()) {
            try {
                BigDecimal minPrice = new BigDecimal(minPriceParam);
                BigDecimal maxPrice = new BigDecimal(maxPriceParam);
                books = books.stream()
                        .filter(book -> book.getPrice().compareTo(minPrice) >= 0 && book.getPrice().compareTo(maxPrice) <= 0)
                        .collect(Collectors.toList());
                model.addAttribute("minPrice", minPrice);
                model.addAttribute("maxPrice", maxPrice);
            } catch (NumberFormatException e) {
                // Invalid price, ignore filter
            }
        }
        model.addAttribute("books", books);
        return "books";
    }
}
