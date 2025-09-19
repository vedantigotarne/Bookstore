package com.example.bookstore.controller;


import com.example.bookstore.dao.BookDAO;
import com.example.bookstore.dao.CategoryDAO;
import com.example.bookstore.dao.OrderDAO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.model.Order;
//import com.example.bookstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//import jakarta.servlet.http.HttpSession;
//import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private OrderDAO orderDAO;

    @GetMapping({"", "/dashboard"})
    public String showDashboard(Model model, @ModelAttribute("categories") List<Category> categories) {
        List<Book> books = bookDAO.getAllBooks();
        List<Order> orders = orderDAO.getAllOrders();
        model.addAttribute("bookCount", books.size());
        model.addAttribute("categoryCount", categories.size());
        model.addAttribute("orderCount", orders.size());
        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String showBooks(Model model) {
        List<Book> books = bookDAO.getAllBooks();
        model.addAttribute("books", books);
        return "admin/books";
    }

    @GetMapping("/categories")
    public String showCategories(Model model) {
        // The "categories" attribute is automatically added by GlobalControllerAdvice
        return "admin/categories";
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {
        List<Order> orders = orderDAO.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/add-book")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/add-book";
    }

    @GetMapping("/edit-book")
    public String showEditBookForm(@RequestParam int id, Model model) {
        Book book = bookDAO.getBookById(id);
        if (book == null) return "redirect:/admin/books";
        model.addAttribute("book", book);
        return "admin/edit-book";
    }

    @GetMapping("/add-category")
    public String showAddCategoryForm() {
        return "admin/add-category";
    }

    @GetMapping("/edit-category")
    public String showEditCategoryForm(@RequestParam int id, Model model) {
        Category category = categoryDAO.getCategoryById(id);
        if (category == null) return "redirect:/admin/categories";
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/add-book")
    public String addBook(@ModelAttribute Book book, Model model) {
        if (book.getTitle().trim().isEmpty() || book.getAuthor().trim().isEmpty() || book.getPrice() == null || book.getCategoryId() == 0) {
           model.addAttribute("errorMessage", "Please fill in all required fields.");
            return "admin/add-book";
        }
        boolean success = bookDAO.addBook(book);
        if (success) return "redirect:/admin/books";
        model.addAttribute("errorMessage", "Failed to add book. Please try again.");
        return "admin/add-book";
    }

    @PostMapping("/edit-book")
    public String updateBook(@ModelAttribute Book book, Model model) {
        if (book.getId() == 0 || book.getTitle().trim().isEmpty() || book.getAuthor().trim().isEmpty() || book.getPrice() == null || book.getCategoryId() == 0) {
            model.addAttribute("errorMessage", "Please fill in all required fields.");
            return "admin/edit-book";
        }
        boolean success = bookDAO.updateBook(book);
        if (success) return "redirect:/admin/books";
        model.addAttribute("errorMessage", "Failed to update book. Please try again.");
        return "admin/edit-book";
    }

    @PostMapping("/delete-book")
    public String deleteBook(@RequestParam int id) {
        bookDAO.deleteBook(id);
        return "redirect:/admin/books";
    }

    @PostMapping("/add-category")
    public String addCategory(@ModelAttribute Category category, Model model) {
        if (category.getName().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please enter a category name.");
            return "admin/add-category";
        }
        boolean success = categoryDAO.addCategory(category);
        if (success) return "redirect:/admin/categories";
        model.addAttribute("errorMessage", "Failed to add category. Please try again.");
        return "admin/add-category";
    }

    @PostMapping("/edit-category")
    public String updateCategory(@ModelAttribute Category category, Model model) {
        if (category.getId() == 0 || category.getName().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please fill in all required fields.");
            return "admin/edit-category";
        }
        boolean success = categoryDAO.updateCategory(category);
        if (success) return "redirect:/admin/categories";
        model.addAttribute("errorMessage", "Failed to update category. Please try again.");
        return "admin/edit-category";
    }

    @PostMapping("/delete-category")
    public String deleteCategory(@RequestParam int id) {
        categoryDAO.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam int orderId,
                                    @RequestParam String status) {
        orderDAO.updateOrderStatus(orderId, status);
        return "redirect:/admin/orders";
    }
}
