# Bookstore Application

A full-featured online bookstore built with Spring Boot, MySQL, Thymeleaf, and Spring Security.

## One-Click Deployment

Deploy this project with one click:

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/YOUR-USERNAME/bookstore)

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/YOUR-USERNAME/bookstore)

## Features

- User authentication and authorization
- Book browsing and searching
- Shopping cart functionality
- Checkout process
- Order management
- Responsive design

## Technologies Used

- **Backend**: Spring Boot 3.3.2, Java 17
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: MySQL
- **Security**: Spring Security
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+ or use the included Maven wrapper
- MySQL 8.0+
- Docker and Docker Compose (optional)

### Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/bookstore.git
   cd bookstore
   ```

2. Configure the database:
   - Create a MySQL database named `bookstore`
   - Update `src/main/resources/application.properties` with your database credentials if needed

3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Access the application at: http://localhost:8080

### Docker Deployment

1. Build and start containers:
   ```bash
   docker-compose up -d
   ```

2. Access the application at: http://localhost:8080

## Deployment Options

### Railway App

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template/712ru3)

### Render

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy)

### Heroku

```bash
heroku create
git push heroku main
```

## Project Structure

- `src/main/java/com/example/bookstore/`: Java source code
  - `controller/`: HTTP controllers
  - `model/`: Domain objects
  - `dao/`: Data access objects
  - `util/`: Utility classes
- `src/main/resources/`: Non-Java resources
  - `static/`: Static assets (CSS, JS, images)
  - `templates/`: Thymeleaf templates
  - `application.properties`: Application configuration
  - `schema.sql`: Database schema

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Author

Your Name - [your.email@example.com](mailto:your.email@example.com)

## Acknowledgments

- Spring Boot Team for the excellent framework
- All open-source libraries used in this project
