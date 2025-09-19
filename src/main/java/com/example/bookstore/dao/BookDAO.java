package com.example.bookstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.bookstore.model.Book;
import com.example.bookstore.util.DBConnection;

/**
 * Data Access Object for Book entities
 */
@Repository
public class BookDAO {
    
    /**
     * Get all books from database
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, c.name as category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Get books by category
     * @param categoryId the category ID
     * @return List of books in the specified category
     */
    public List<Book> getBooksByCategory(int categoryId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, c.name as category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.id " +
                     "WHERE b.category_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting books by category: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Get book by ID
     * @param id the book ID
     * @return Book object or null if not found
     */
    public Book getBookById(int id) {
        String sql = "SELECT b.*, c.name as category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.id " +
                     "WHERE b.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting book by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Search books by title or author
     * @param searchTerm the search term
     * @return List of books matching the search term
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, c.name as category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.id " +
                     "WHERE b.title LIKE ? OR b.author LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String term = "%" + searchTerm + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Add a new book
     * @param book the book to add
     * @return true if successful, false otherwise
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, description, price, category_id, quantity, image_url, rating) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setBigDecimal(4, book.getPrice());
            pstmt.setInt(5, book.getCategoryId());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setString(7, book.getImageUrl());
            pstmt.setDouble(8, book.getRating());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update an existing book
     * @param book the book to update
     * @return true if successful, false otherwise
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, description = ?, price = ?, " +
                     "category_id = ?, quantity = ?, image_url = ?, rating = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setBigDecimal(4, book.getPrice());
            pstmt.setInt(5, book.getCategoryId());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setString(7, book.getImageUrl());
            pstmt.setDouble(8, book.getRating());
            pstmt.setInt(9, book.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a book
     * @param id the book ID
     * @return true if successful, false otherwise
     */
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get top rated books
     * @param limit the number of books to return
     * @return List of top rated books
     */
    public List<Book> getTopRatedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, c.name as category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.id " +
                     "ORDER BY b.rating DESC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting top rated books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Helper method to extract a Book from a ResultSet
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setDescription(rs.getString("description"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setCategoryId(rs.getInt("category_id"));
        book.setCategoryName(rs.getString("category_name"));
        book.setQuantity(rs.getInt("quantity"));
        book.setImageUrl(rs.getString("image_url"));
        book.setRating(rs.getDouble("rating"));
        return book;
    }
}