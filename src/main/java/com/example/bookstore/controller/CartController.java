package com.example.bookstore.controller;

import com.example.bookstore.dao.BookDAO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.util.SessionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private BookDAO bookDAO;

    @GetMapping("")
    public String showCart(Model model, HttpSession session) {
        List<CartItem> cart = getCart(session);
        BigDecimal total = calculateCartTotal(cart);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("")
    public String handleCartAction(@RequestParam String action,
                                   @RequestParam(required = false) Integer bookId,
                                   @RequestParam(required = false) Integer quantity,
                                   HttpSession session) {
        List<CartItem> cart = getCart(session);
        switch (action) {
            case "add":
                addToCart(bookId, quantity, cart);
                break;
            case "update":
                updateCart(bookId, quantity, cart);
                break;
            case "remove":
                removeFromCart(bookId, cart);
                break;
            case "clear":
                clearCart(session);
                return "redirect:/cart"; // Return early as session attribute is already removed
            default:
                // Unknown action, do nothing
                return "redirect:/cart";
        }
        // Save the modified cart back to the session
        session.setAttribute(SessionConstants.CART_ATTRIBUTE, cart);
        return "redirect:/cart";
    }

    private List<CartItem> getCart(HttpSession session) {
    @SuppressWarnings("unchecked")
    List<CartItem> cart = (List<CartItem>) session.getAttribute(SessionConstants.CART_ATTRIBUTE);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(SessionConstants.CART_ATTRIBUTE, cart);
        }
        return cart;
    }

    private void addToCart(Integer bookId, Integer quantity, List<CartItem> cart) {
        if (bookId == null || quantity == null) return;
        if (quantity <= 0) quantity = 1;
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getBook().getId() == bookId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                CartItem item = new CartItem(book, quantity);
                cart.add(item);
            }
        }
    }

    private void updateCart(Integer bookId, Integer quantity, List<CartItem> cart) {
        if (bookId == null || quantity == null) return;
        if (quantity <= 0) {
            cart.removeIf(item -> item.getBook().getId() == bookId);
        } else {
            for (CartItem item : cart) {
                if (item.getBook().getId() == bookId) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
    }

    private void removeFromCart(Integer bookId, List<CartItem> cart) {
        if (bookId == null) return;
        cart.removeIf(item -> item.getBook().getId() == bookId);
    }

    private void clearCart(HttpSession session) {
        session.removeAttribute(SessionConstants.CART_ATTRIBUTE);
    }

    private BigDecimal calculateCartTotal(List<CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}
