package com.example.bookstore.controller;

import com.example.bookstore.dao.BookDAO;
import com.example.bookstore.dao.CategoryDAO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/book")
public class BookDetailController {

    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private CategoryDAO categoryDAO;

    @GetMapping("/{id}")
    public String showBookDetail(@PathVariable int id, Model model) {
        Book book = bookDAO.getBookById(id);
        if (book == null) {
            return "redirect:/books";
        }
        Category category = categoryDAO.getCategoryById(book.getCategoryId());
        model.addAttribute("category", category);
        List<Book> relatedBooks = bookDAO.getBooksByCategory(book.getCategoryId());
        relatedBooks.removeIf(b -> b.getId() == book.getId());
        if (relatedBooks.size() > 4) {
            relatedBooks = relatedBooks.subList(0, 4);
        }
        model.addAttribute("book", book);
        model.addAttribute("relatedBooks", relatedBooks);
        return "book-detail";
    }
}
