package com.example.bookstore.util;

/**
 * A utility class to hold constants for session attribute names.
 * This helps avoid "magic strings" and ensures consistency across the application.
 */
public final class SessionConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SessionConstants() {}

    /**
     * The session attribute key for the logged-in user object.
     */
    public static final String USER_ATTRIBUTE = "user";

    /**
     * The session attribute key for the shopping cart list.
     */
    public static final String CART_ATTRIBUTE = "cart";

    /**
     * The session attribute key for storing a URL to redirect to after login.
     */
    public static final String REDIRECT_URL_ATTRIBUTE = "redirectURL";
}