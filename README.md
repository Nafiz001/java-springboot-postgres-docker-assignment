# Student and Course Management System

A comprehensive Spring Boot application with PostgreSQL, implementing MVC architecture, REST APIs, and role-based authentication.

## Features

- **Role-Based Authentication & Authorization**
  - Three user roles: STUDENT, TEACHER, ADMIN
  - Spring Security with BCrypt password encryption
  
- **User Management**
  - User registration and login
  - Role-specific dashboards
  
- **Course Management**
  - Teachers can create, update, and delete courses
  - Students can view and enroll in courses
  - Admins have full access
  
- **Enrollment System**
  - Students can enroll in courses
  - Teachers can view enrolled students and assign grades
  - Track enrollment status (ACTIVE, COMPLETED, DROPPED)

- **Database Relations**
  - One-to-Many: Teacher → Courses
  - One-to-Many: Student → Enrollments
  - Many-to-One: Enrollments → Course

## Technology Stack

- **Backend**: Spring Boot 4.0.2, Java 17
- **Database**: PostgreSQL 15
- **Security**: Spring Security with BCrypt
- **Frontend**: Thymeleaf, HTML5, CSS3
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Project Structure

```
SEPM_Assignment/
├── src/
│   ├── main/
│   │   ├── java/com/example/sepm_assignment/
│   │   │   ├── config/         # Security & Configuration
│   │   │   ├── controller/     # MVC Controllers
│   │   │   │   └── api/        # REST API Controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── model/          # Entity Classes
│   │   │   ├── repository/     # JPA Repositories
│   │   │   └── service/        # Business Logic
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf Templates
│   │       └── application.properties
├── Dockerfile
├── compose.yaml
└── pom.xml
```

## Getting Started

### Prerequisites

- Docker Desktop installed
- Java 17 (for local development)
- Maven 3.9+ (for local development)

### Running with Docker

1. **Build and start the application:**
   ```powershell
   docker-compose up --build
   ```

2. **Access the application:**
   - Open browser: http://localhost:8081
   - The application will automatically create default users

3. **Stop the application:**
   ```powershell
   docker-compose down
   ```

4. **Stop and remove volumes (reset database):**
   ```powershell
   docker-compose down -v
   ```

### Running Locally (Without Docker)

1. **Start PostgreSQL:**
   ```powershell
   docker-compose up postgres
   ```

2. **Run the Spring Boot application:**
   ```powershell
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

## Default Users

The application creates three default users on first startup:

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| teacher  | teacher123  | TEACHER |
| student  | student123  | STUDENT |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user

### Users (Admin only)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/role/{role}` - Get users by role
- `DELETE /api/users/{id}` - Delete user

### Courses
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `GET /api/courses/teacher/{teacherId}` - Get courses by teacher
- `POST /api/courses` - Create course (Teacher/Admin)
- `PUT /api/courses/{id}` - Update course (Teacher/Admin)
- `DELETE /api/courses/{id}` - Delete course (Teacher/Admin)

### Enrollments
- `POST /api/enrollments` - Enroll student (Student/Admin)
- `GET /api/enrollments/student/{studentId}` - Get student enrollments
- `GET /api/enrollments/course/{courseId}` - Get course enrollments (Teacher/Admin)
- `PUT /api/enrollments/{id}/grade` - Update grade (Teacher/Admin)
- `PUT /api/enrollments/{id}/status` - Update status (Teacher/Admin)
- `DELETE /api/enrollments/{id}` - Delete enrollment (Admin)

## Web Pages

- `/` - Home (redirects to login)
- `/login` - Login page
- `/register` - Registration page
- `/dashboard` - Role-based dashboard
- `/courses` - Course list
- `/courses/create` - Create course (Teacher/Admin)
- `/courses/{id}` - Course details
- `/courses/{id}/edit` - Edit course (Teacher/Admin)

## Database Schema

### Users Table
- id (PK)
- username (unique)
- password (encrypted)
- email (unique)
- full_name
- role (STUDENT, TEACHER, ADMIN)
- enabled

### Courses Table
- id (PK)
- course_code (unique)
- course_name
- description
- credits
- teacher_id (FK → users)

### Enrollments Table
- id (PK)
- student_id (FK → users)
- course_id (FK → courses)
- enrollment_date
- status (ACTIVE, COMPLETED, DROPPED)
- grade

## Role-Based Access Control

### Student Role
- View all courses
- Enroll in courses
- View own enrollments and grades
- Cannot create/modify courses

### Teacher Role
- Create, edit, and delete own courses
- View enrolled students in own courses
- Assign grades to students
- Update enrollment status

### Admin Role
- Full access to all features
- Manage users
- Manage all courses
- View all enrollments
- Delete users and enrollments

## Development

### Build the project
```powershell
./mvnw clean install
```

### Run tests
```powershell
./mvnw test
```

### Package as JAR
```powershell
./mvnw clean package
```

## Docker Commands

### Build image
```powershell
docker build -t sepm-assignment .
```

### Run with Docker Compose
```powershell
docker-compose up -d
```

### View logs
```powershell
docker-compose logs -f app
```

### Access PostgreSQL CLI
```powershell
docker-compose exec postgres psql -U myuser -d mydatabase
```

## Security Features

- Password encryption using BCrypt
- CSRF protection for web forms (disabled for REST APIs)
- Role-based method security with @PreAuthorize
- Session management
- Secure logout

## Future Enhancements

- Add Liquibase for database migrations
- Implement pagination for large datasets
- Add email notifications
- File upload for course materials
- Student attendance tracking
- Assignment submission system
- Grade calculation and GPA tracking

## License

This project is for educational purposes (SEPM Assignment).

## Contact

For questions or issues, please contact the development team.
