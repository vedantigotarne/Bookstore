package com.example.bookstore.controller;

import com.example.bookstore.dao.BookDAO;
import com.example.bookstore.model.Book; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private BookDAO bookDAO;

    @GetMapping("")
    public String showHome(Model model) {
        List<Book> featuredBooks = bookDAO.getTopRatedBooks(6);
        model.addAttribute("featuredBooks", featuredBooks);
        return "home";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("term") String searchTerm, Model model) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<Book> searchResults = bookDAO.searchBooks(searchTerm);
            model.addAttribute("books", searchResults);
            model.addAttribute("searchTerm", searchTerm);
            // Note: The 'categories' attribute is automatically added by GlobalControllerAdvice
            return "books";
        } else {
            // If search term is empty, redirect to the main book list
            return "redirect:/books";
        }
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContactForm(@RequestParam String name, @RequestParam String email, @RequestParam String subject, @RequestParam String message, RedirectAttributes redirectAttributes) {
        // In a real application, you would process this data (e.g., send an email)
        System.out.println("Contact form submission received for: " + email);
        // Add a success message to be displayed on the contact page after redirect
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your message! We will get back to you shortly.");
        return "redirect:/contact";
    }

    @GetMapping("/terms")
    public String showTermsPage() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String showPrivacyPage() {
        return "privacy";
    }
}
