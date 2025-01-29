# Employee Records Management System

## Project Goal

The Employee Records Management System (ERMS) is designed to centralize the management of employee data across various departments within an organization. This project is part of the screening process for a job I applied to. The system aims to provide a user-friendly interface for HR personnel and managers to perform CRUD operations on employee records while ensuring data integrity and security. The application will also include features for auditing changes, searching and filtering employee data, and generating reports.

## Features

- Manage employee attributes: Full Name, Employee ID, Job Title, Department, Hire Date, Employment Status, Contact Information, Address.
- User roles and permissions:
  - **HR Personnel**: Full CRUD access to all employee data.
  - **Managers**: View and update specific employee details within their department.
  - **Administrators**: Full system access, including configuration settings and user permissions management.
- Audit trail to log all changes made to employee records.
- Search and filtering options for employee data.
- Validation rules to ensure data integrity.
- Basic reporting capabilities.

## Tasks to be Done

 **Backend Development**
  - [X] Implement RESTful API using Spring Boot.
  - [X] Create Swagger documentation for the API.
  - [X] Employee CRUD Operations
  - [X] Search and Filtering for Employee
  - [X] Set up user authentication and authorization (JWT or session-based).
  - [X] Role Based Access Controll
  - [X] Implement logging and audit trail functionality.
  - [X] Implement basic reporting features for employee data.

**Frontend Development**
  - [X] Design and implement the Swing-based desktop UI.
  - [X] Use MigLayout and GridBagLayout for layout management.
  - [X] Create forms for employee CRUD operations.

 **Testing**
  - [X] Write unit tests for service and controller layers using JUnit and Mockito.
  - [X] Validate API endpoints with Postman and create a Postman collection.

  

**Dockerization**
  - [X] Create a Dockerfile for the application.
  - [X] Build and test the Docker image locally.

