package com.example.bookstore.dao;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order entities
 */
@Repository
public class OrderDAO {
    
    /**
     * Create a new order
     * @param order the order to create
     * @return true if successful, false otherwise
     */
    public boolean createOrder(Order order) {
        String orderSql = "INSERT INTO orders (user_id, total_amount, shipping_address, payment_method) " +
                          "VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert order
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setBigDecimal(2, order.getTotalAmount());
            orderStmt.setString(3, order.getShippingAddress());
            orderStmt.setString(4, order.getPaymentMethod());
            
            int affectedRows = orderStmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
            
            // Insert order items
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                String itemSql = "INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                
                for (OrderItem item : order.getOrderItems()) {
                    itemStmt.setInt(1, order.getId());
                    itemStmt.setInt(2, item.getBookId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setBigDecimal(4, item.getPrice());
                    itemStmt.addBatch();
                    
                    // Update book quantity
                    String updateBookSql = "UPDATE books SET quantity = quantity - ? WHERE id = ?";
                    PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                    updateBookStmt.setInt(1, item.getQuantity());
                    updateBookStmt.setInt(2, item.getBookId());
                    updateBookStmt.executeUpdate();
                }
                
                itemStmt.executeBatch();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get order by ID
     * @param id the order ID
     * @return Order object or null if not found
     */
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItemsByOrderId(order.getId()));
                return order;
            }
        } catch (SQLException e) {
            System.out.println("Error getting order by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get orders by user ID
     * @param userId the user ID
     * @return List of orders for the specified user
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Error getting orders by user ID: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get all orders (for admin)
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Update order status
     * @param orderId the order ID
     * @param status the new status
     * @return true if successful, false otherwise
     */
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET order_status = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get order items by order ID
     * @param orderId the order ID
     * @return List of order items for the specified order
     */
    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, b.title, b.author, b.image_url FROM order_items oi " +
                     "JOIN books b ON oi.book_id = b.id " +
                     "WHERE oi.order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setBookId(rs.getInt("book_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                
                // Set book information
                Book book = new Book();
                book.setId(item.getBookId());
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setImageUrl(rs.getString("image_url"));
                item.setBook(book);
                
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error getting order items: " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Helper method to extract an Order from a ResultSet
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setOrderStatus(rs.getString("order_status"));
        return order;
    }
}