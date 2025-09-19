package com.example.bookstore.controller;


import com.example.bookstore.dao.OrderDAO;
import com.example.bookstore.dao.UserDAO;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.User;
import com.example.bookstore.util.SessionConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        if (session.getAttribute(SessionConstants.USER_ATTRIBUTE) != null) {
            return "redirect:/user/account";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session, Model model) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please enter username and password.");
           return "login";
        }
        User user = userDAO.authenticate(email, password);
        if (user != null) {
            session.setAttribute(SessionConstants.USER_ATTRIBUTE, user);
            String redirectURL = (String) session.getAttribute(SessionConstants.REDIRECT_URL_ATTRIBUTE);
            if (redirectURL != null) {
                session.removeAttribute(SessionConstants.REDIRECT_URL_ATTRIBUTE);
                return "redirect:/" + redirectURL;
            } else {
                return "redirect:/user/account";
            }
        } else {
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(HttpSession session, Model model) {
        if (session.getAttribute(SessionConstants.USER_ATTRIBUTE) != null) {
            return "redirect:/user/account";
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String confirmPassword,
                               HttpSession session, Model model) {
        if (user.getUsername() == null || user.getPassword() == null || confirmPassword == null || user.getEmail() == null ||
            user.getUsername().trim().isEmpty() || user.getPassword().trim().isEmpty() || confirmPassword.trim().isEmpty() || user.getEmail().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please fill in all required fields.");
            return "register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "register";
        }
        if (userDAO.isUsernameExists(user.getUsername())) {
            model.addAttribute("errorMessage", "Username already exists. Please choose a different one.");
            return "register";
        }
        if (userDAO.isEmailExists(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already exists. Please use a different one.");
            return "register";
        }
        user.setAdmin(false);
        boolean success = userDAO.registerUser(user);
        if (success) {
            session.setAttribute(SessionConstants.USER_ATTRIBUTE, user);
            return "redirect:/user/account";
        } else {
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "register";
        }
    }

    @GetMapping("/account")
    public String showAccountPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (user == null) {
            return "redirect:/user/login";
        }
        List<Order> allOrders = orderDAO.getOrdersByUserId(user.getId());
        // For the account page, we only show the most recent orders.
        List<Order> recentOrders = allOrders.stream().limit(5).collect(Collectors.toList());
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("user", user);
        return "account";
    }

    @PostMapping("/update-profile")
    public String updateUserAccount(@ModelAttribute User formUser,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (sessionUser == null) {
            return "redirect:/user/login";
        }
        // Update user details from the form, but keep sensitive/unchangeable data
        // Note: Password updates should be handled in a separate, secure form and should be hashed.
        sessionUser.setUsername(formUser.getUsername());
        sessionUser.setFullName(formUser.getFullName());
        sessionUser.setEmail(formUser.getEmail());
        sessionUser.setAddress(formUser.getAddress());
        sessionUser.setPhone(formUser.getPhone());

        boolean success = userDAO.updateUser(sessionUser);
        if (success) {
            session.setAttribute(SessionConstants.USER_ATTRIBUTE, sessionUser);
            redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update your profile. Please try again.");
        }
        return "redirect:/user/account";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmNewPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (sessionUser == null) {
            return "redirect:/user/login";
        }

        // 1. Verify the current password is correct
        if (!passwordEncoder.matches(currentPassword, sessionUser.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect current password.");
            return "redirect:/user/account";
        }

        // 2. Verify the new password and confirmation match
        if (!newPassword.equals(confirmNewPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/user/account";
        }

        // 3. Hash and update the password
        String newHashedPassword = passwordEncoder.encode(newPassword);
        boolean success = userDAO.updatePassword(sessionUser.getId(), newHashedPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
            sessionUser.setPassword(newHashedPassword); // Update password in session object
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password. Please try again.");
        }
        return "redirect:/user/account";
    }

    @GetMapping("/orders")
    public String showOrdersPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (user == null) {
            return "redirect:/user/login";
        }
        List<Order> orders = orderDAO.getOrdersByUserId(user.getId());
        model.addAttribute("orderList", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String showOrderDetailPage(@PathVariable("id") int orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (user == null) {
            return "redirect:/user/login";
        }
        // Note: This assumes you have an `getOrderById` method in your OrderDAO.
        // In a real app, you'd also verify the order belongs to the logged-in user.
        Order order = orderDAO.getOrderById(orderId);
        if (order != null && order.getUserId() == user.getId()) {
            model.addAttribute("order", order);
            return "order-confirmation"; // Re-using the confirmation page to show details
        }
        return "redirect:/user/orders"; // Redirect if order not found or doesn't belong to user
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
