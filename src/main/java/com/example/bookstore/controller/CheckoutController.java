package com.example.bookstore.controller;

import com.example.bookstore.dao.OrderDAO;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.User;
import com.example.bookstore.util.SessionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private OrderDAO orderDAO;

    @GetMapping("")
    public String showCheckout(Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (user == null) {
            session.setAttribute(SessionConstants.REDIRECT_URL_ATTRIBUTE, "checkout");
            return "redirect:/user/login";
        }
    @SuppressWarnings("unchecked")
    List<CartItem> cart = (List<CartItem>) session.getAttribute(SessionConstants.CART_ATTRIBUTE);
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }
        BigDecimal total = calculateCartTotal(cart);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "checkout";
    }

    @PostMapping("")
    public String processCheckout(@RequestParam String shippingAddress,
                                  @RequestParam String paymentMethod,
                                  Model model, HttpSession session) {
        User user = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (user == null) {
            return "redirect:/user/login";
        }
    @SuppressWarnings("unchecked")
    List<CartItem> cart = (List<CartItem>) session.getAttribute(SessionConstants.CART_ATTRIBUTE);
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }
        if (shippingAddress == null || shippingAddress.trim().isEmpty() ||
            paymentMethod == null || paymentMethod.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please fill in all required fields.");
            return showCheckout(model, session);
        }
        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderDate(new Date());
        order.setTotalAmount(calculateCartTotal(cart));
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus("Pending");
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(cartItem.getBook().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        boolean success = orderDAO.createOrder(order);
        if (success) {
            session.removeAttribute(SessionConstants.CART_ATTRIBUTE);
            model.addAttribute("order", order);
            return "order-confirmation";
        } else {
            model.addAttribute("errorMessage", "An error occurred while processing your order. Please try again.");
            return showCheckout(model, session);
        }
    }

    private BigDecimal calculateCartTotal(List<CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart != null) {
            for (CartItem item : cart) {
                total = total.add(item.getSubtotal());
            }
        }
        return total;
    }
}
