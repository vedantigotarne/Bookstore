# Bookstore Project Deployment Guide

This guide will help you deploy your Bookstore project to various platforms to showcase on your resume.

## Option 1: Render.com (Free Tier Available)

Render offers a free tier that's perfect for showcasing portfolio projects.

### Steps for Render Deployment:

1. **Create a Render account** at [render.com](https://render.com)

2. **Configure your Web Service**:
   - Connect your GitHub repository
   - Select "Web Service"
   - Select the branch to deploy
   - Set build command: `./mvnw clean package -DskipTests`
   - Set start command: `java -jar -Dspring.profiles.active=prod target/*.jar`

3. **Add Environment Variables**:
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - `MYSQL_HOST`: Your database host (Render PostgreSQL or external MySQL)
   - `MYSQL_PORT`: Database port
   - `MYSQL_DB`: Database name
   - `MYSQL_USER`: Database username
   - `MYSQL_PASSWORD`: Database password

4. **Set up a Database**:
   - Create a PostgreSQL database on Render
   - OR use an external MySQL database (PlanetScale offers a free tier)

## Option 2: Railway.app (Credit System)

Railway offers a $5 credit monthly which is enough for small projects.

### Steps for Railway Deployment:

1. **Create a Railway account** at [railway.app](https://railway.app)

2. **Initialize a new project**:
   - Connect your GitHub repository
   - Deploy the repository

3. **Add a MySQL Database**:
   - Add a new service → Database → MySQL

4. **Configure Environment Variables**:
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - Railway will automatically inject database connection variables

5. **Deploy**:
   - Railway will automatically deploy when you push to your repository

## Option 3: GitHub Pages + Mock Backend (Static Demo)

If you want to showcase just the UI without a live backend:

1. Create a branch in your project called `static-demo`
2. Modify your frontend code to use mock data instead of real API calls
3. Build a static version of your site:
   ```bash
   # Add a build script to generate static HTML from your Thymeleaf templates
   ```
4. Push to GitHub and enable GitHub Pages

## Option 4: AWS Free Tier

AWS offers 12 months of free tier services:

1. **Create an EC2 Instance**:
   - Use t2.micro (free tier eligible)
   - Select Amazon Linux 2

2. **Install Java and Docker**:
   ```bash
   sudo yum update -y
   sudo amazon-linux-extras install java-openjdk11 -y
   sudo amazon-linux-extras install docker -y
   sudo service docker start
   sudo usermod -a -G docker ec2-user
   ```

3. **Upload Your Project**:
   - Use SCP or Git to get your code on the server

4. **Deploy Using Docker Compose**:
   ```bash
   cd bookstore
   docker-compose up -d
   ```

## Resume Tips

When adding this project to your resume:

1. **Create a dedicated project page** with:
   - Screenshots
   - Feature list
   - Technical challenges overcome
   - Architecture diagram

2. **Include a QR code** on your resume that links to the live demo

3. **Highlight technical skills** used in the project:
   - Java
   - Spring Boot
   - MySQL
   - Thymeleaf
   - Docker
   - CI/CD

4. **Add GitHub repository link** for recruiters to review your code

5. **Prepare to discuss**:
   - Design decisions
   - Technical challenges
   - What you learned
   - How you'd improve it with more time/resources
