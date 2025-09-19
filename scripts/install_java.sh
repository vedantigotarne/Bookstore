#!/bin/bash

# Update system
sudo yum update -y

# Install Java 17
sudo yum install -y java-17-openjdk-devel

# Verify Java installation
java -version
