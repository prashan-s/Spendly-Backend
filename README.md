[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/xIbq4TFL)

---

# Spendly

Spendly is a secure RESTful API for a Personal Finance Tracker. Built as a monolithic Spring Boot application using Kotlin and MongoDB, Spendly allows users to manage financial records, track expenses and income, set budgets, generate reports, and track savings goals. The API features robust security with JWT authentication, email verification during user registration, and scheduled tasks for recurring operations.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Email Verification Process](#email-verification-process)
- [License](#license)

---

## Features

- **User Authentication & Authorization**
    - JWT-based login and registration
    - Role-based access control (Admin & Regular User)
    - Email verification on registration

- **Expense & Income Tracking**
    - CRUD operations for transactions
    - Support for categorizing and tagging transactions
    - Handling recurring transactions with scheduling

- **Budget Management**
    - Set monthly or category-specific budgets
    - Alerts when approaching or exceeding budgets
    - Recommendations based on spending trends

- **Financial Reporting**
    - Aggregated reports for income, expenses, and net savings
    - Breakdown of expenses by category

- **Goals & Savings Tracking**
    - Set financial goals and track progress
    - Automatic savings allocation options

- **Multi-Currency Support**
    - Store and convert transactions in multiple currencies
    - Scheduled update of exchange rates from an external API

- **Notifications & Alerts**
    - In-app notifications for budget alerts, recurring reminders, and unusual spending patterns

- **Role-Based Dashboard**
    - Tailored dashboard data for Admin (system overview) and Regular Users (personal financial summary)

---

## Tech Stack

- **Backend:** Spring Boot, Kotlin
- **Database:** MongoDB
- **Security:** Spring Security, JWT
- **Email:** Spring Boot Starter Mail
- **Scheduling:** Spring Scheduling
- **Documentation:** Springdoc OpenAPI (Swagger UI)
- **Build Tool:** Gradle (Kotlin DSL)

---

## Project Structure

```
com.spendly.backend
│
├── advice                # Global exception handling
├── config                # Security and other configuration classes
├── controller            # REST controllers (Auth, Transactions, Budgets, Reports, etc.)
├── dto                   # Data Transfer Objects for requests/responses
├── entity                # MongoDB document entities (User, Transaction, Budget, Goal, etc.)
├── filter                # Security filters (JWT Authentication Filter)
├── repository            # MongoDB repositories
├── service               # Service interfaces (prefixed with I) and implementations
└── util                  # Utility classes (JWT utilities, etc.)
```

---

## Setup Instructions

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/spendly.git
   cd spendly
   ```

2. **Configure Application Settings:**

    - Update `src/main/resources/application.yml` with your MongoDB URI, JWT secret, and email settings.
    - Example:
      ```yaml
      spring:
        data:
          mongodb:
            uri: mongodb://localhost:27017/spendly
        mail:
          host: smtp.example.com
          port: 587
          username: your_email@example.com
          password: your_email_password
          properties:
            mail.smtp.auth: true
            mail.smtp.starttls.enable: true
      
      jwt:
        secret: your_jwt_secret_here
        expiration: 86400000
      
      app:
        verification:
          base-url: "http://localhost:8080"
      ```

3. **Build the Project:**

   ```bash
   ./gradlew clean build
   ```

4. **Run the Application:**

   ```bash
   ./gradlew bootRun
   ```

   The API will be available at `http://localhost:8080/api/v1`.

---

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

This interactive documentation details all endpoints, request/response schemas, and error codes.

---

## Testing

- **Unit Tests:** Run using:
  ```bash
  ./gradlew test
  ```
- **Integration Tests:** Spring Boot tests ensure controller, service, and repository layers work together.
- **Security Tests:** Verify JWT authentication and role-based access with Spring Security test support.

---

## Email Verification Process

1. **Registration:**
    - When a user registers, a verification token is generated and stored along with the user's record.
    - An email is sent via Spring Boot Mail with a link like:  
      `http://localhost:8080/api/auth/verify?token=<verificationToken>`

2. **Verification Endpoint:**
    - Clicking the link calls the verification endpoint which marks the user as enabled and removes the token.

3. **Login Restriction:**
    - Unverified users cannot log in until they complete the email verification.

For email sending, ensure your SMTP settings in `application.yml` are correctly configured. The email service uses `JavaMailSender` to dispatch the verification message.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---