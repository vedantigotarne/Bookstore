package com.example.bookstore.model;

import java.math.BigDecimal;

/**
 * CartItem model class representing an item in a shopping cart
 */
public class CartItem {
    private Book book;
    private int quantity;
    
    public CartItem() {
    }
    
    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getSubtotal() {
        return book.getPrice().multiply(new BigDecimal(quantity));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CartItem cartItem = (CartItem) o;
        
        return book != null ? book.getId() == cartItem.book.getId() : cartItem.book == null;
    }
    
    @Override
    public int hashCode() {
        return book != null ? book.getId() : 0;
    }
} 