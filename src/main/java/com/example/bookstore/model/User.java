package com.example.bookstore.model;

import java.sql.Date;

/**
 * User model class representing a user in the system
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String address;
    private String phone;
    private boolean isAdmin;
    private String profileImage;
    private String role;
    
    public User() {
    }
    
    public User(int id, String username, String password, String email, String fullName, 
                String address, String phone, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
    public Date getRegistrationDate() {
        return registrationDate();
    }

    private Date registrationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRegistrationDate(Date registrationDate) {
    }
	
	public String getRole() {
	    return role;
	}

	public void setRole(String role) {
	    this.role = role;
	}
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
} 