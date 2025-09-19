#!/bin/bash

# Install MySQL
sudo yum install -y mysql-server

# Start MySQL
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Set root password and create database
sudo mysql_secure_installation <<EOF

y
2005
2005
y
y
y
y
EOF

# Create database and user
mysql -u root -p2005 <<EOF
CREATE DATABASE bookstore;
CREATE USER 'bookstore_user'@'localhost' IDENTIFIED BY 'bookstore_password';
GRANT ALL PRIVILEGES ON bookstore.* TO 'bookstore_user'@'localhost';
FLUSH PRIVILEGES;
EOF

# Import schema
mysql -u bookstore_user -pbookstore_password bookstore < ../src/main/resources/schema.sql
