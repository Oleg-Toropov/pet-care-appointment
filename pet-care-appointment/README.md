# Pet Care Appointment

**API Version:** 1.0  
**Description:**  
Pet Care Appointment is a full-featured application designed for managing veterinary appointments, users, pets, and reviews. The project provides a user-friendly interface for pet owners and veterinarians, along with administrative features for efficient system management.

---

## Key Features

1. **Appointment Management:**
    - CRUD operations for scheduling, modifying, deleting, and viewing appointments.
    - Support for managing appointment statuses.

2. **User Management:**
    - Registration and authentication using JWT.
    - Personal dashboards with access to profile information, appointment history, pets, and reviews.
    - Support for uploading and displaying user photos.

3. **Pet Management:**
    - Adding, editing, and deleting pet information.

4. **Reviews and Ratings:**
    - Clients can leave reviews for veterinarians.
    - Calculation of average ratings for veterinarians.

5. **Asynchronous Task Handling:**
    - Notifications about appointments via RabbitMQ.

---

## Technology Stack

### Backend
- **Java 17:** The primary development language.
- **Spring Boot 3.4.1:**
    - **Spring Web:** For building RESTful APIs.
    - **Spring Data JPA:** Database interaction through Hibernate ORM.
    - **Spring Security:** API protection with JWT.
    - **Spring Validation:** Validation of input data.
    - **Spring Mail:** Email notifications.
    - **Spring AMQP:** Integration with RabbitMQ for asynchronous processes.
- **Hibernate:** ORM for database operations.
- **MySQL:** Data storage for users, pets, appointments, and reviews.
- **RabbitMQ:** Asynchronous task handling.
- **Logback:** Event logging within the application.
- **ModelMapper:** Transforming data between DTOs and entities.
- **Javadoc:** Internal documentation for all classes, methods, and APIs, providing a clear understanding of the project's structure and functionality for developers.


### Frontend
- **JavaScript:** The primary language for developing the client-side.
- **React 18:** Library for building component-based interfaces.
- **Axios:** AJAX requests for server interaction.
- **Bootstrap 5.3 and React Bootstrap:** Adaptive design and pre-built UI components.
- **Recharts:** Data visualization with charts and graphs.
- **Framer Motion:** Animations for enhancing user experience.
- **React Router DOM:** Client-side routing for the application.
- **React DatePicker, React DayPicker, and date-fns:** Date and calendar utilities.
- **Font Awesome and React Icons:** Icon integration.
- **React Input Mask:** Input field masking for structured data entry.

### Containerization and CI/CD
- **Docker:** Containerization for streamlined deployment.
- **GitHub Actions:** Automated workflows for building, testing, and deployment.

---
