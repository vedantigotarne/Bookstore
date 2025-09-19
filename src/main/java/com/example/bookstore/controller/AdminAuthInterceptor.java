package com.example.bookstore.controller;

import com.example.bookstore.model.User;
import com.example.bookstore.util.SessionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to protect admin routes.
 * Checks if a user is logged in and has admin privileges before allowing access to /admin/** URLs.
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    /**
     * Intercepts incoming requests to check for admin authentication.
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param handler the chosen handler to execute, for type and/or instance examination
     * @return {@code true} if the user is an admin, {@code false} otherwise.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConstants.USER_ATTRIBUTE) == null) {
            // User is not logged in, redirect to login page.
            response.sendRedirect(request.getContextPath() + "/user/login");
            return false;
        }

        Object userAttr = session.getAttribute(SessionConstants.USER_ATTRIBUTE);
        if (userAttr instanceof User) {
            User user = (User) userAttr;
            if (user.isAdmin()) {
                return true; // User is admin, proceed.
            }
        }

        // User is logged in but not an admin, redirect to home page.
        response.sendRedirect(request.getContextPath() + "/");
        return false;
    }
}