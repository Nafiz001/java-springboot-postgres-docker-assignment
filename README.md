# Student & Teacher Management System
A comprehensive web-based management system for educational institutions built with Spring Boot, PostgreSQL, Spring Security, and Docker.
## Features
- Role-Based Access Control (Admin, Teacher, Student)
- Course Management with cascade deletion
- Student Enrollment System
- Grade Management
- User Management (Admin)
- Custom Error Pages
- REST API with Postman Collection
## Tech Stack
- Java 17
- Spring Boot 3.2.2
- PostgreSQL 15
- Spring Security
- Docker & Docker Compose
- Thymeleaf
- Maven
## Quick Start
### Prerequisites
- Docker Desktop installed
- Docker Compose installed
### Run Application
```bash
docker-compose up --build
```
Access at: http://localhost:8081
## Default Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Teacher | teacher | teacher123 |
| Student | student | student123 |
## API Testing
Import the Postman collection:
```
SEPM_Assignment_API.postman_collection.json
```
### Collection Features
- All REST API endpoints
- Authentication flows
- User management
- Course CRUD operations
- Enrollment management
- Pre-configured base URL
## Project Structure
```
src/
├── main/
│   ├── java/com/example/sepm_assignment/
│   │   ├── config/          # Security & initialization
│   │   ├── controller/      # Web & REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data access layer
│   │   └── service/         # Business logic
│   └── resources/
│       ├── templates/       # Thymeleaf pages
│       └── application.properties
├── compose.yaml             # Docker Compose config
├── Dockerfile
└── pom.xml
```
## API Endpoints
### Authentication
- POST /api/auth/register - Register user
- POST /login - Login
- POST /logout - Logout
### Users
- GET /api/users - Get all users
- GET /api/users/{id} - Get user by ID
- GET /api/users/role/{role} - Get users by role
### Courses
- GET /api/courses - Get all courses
- POST /api/courses - Create course
- PUT /api/courses/{id} - Update course
- DELETE /api/courses/{id} - Delete course (cascade)
### Enrollments
- POST /api/enrollments - Enroll student
- GET /api/enrollments/student/{id} - Get enrollments
- PUT /api/enrollments/{id}/grade - Update grade
- DELETE /api/enrollments/{id} - Delete enrollment
### Dashboards (Web)
- GET /admin/dashboard - Admin panel
- GET /teacher/dashboard - Teacher panel
- GET /student/dashboard - Student panel
## Database Schema
### Users
- id, username, password, email, full_name, role, enabled
### Courses
- id, course_code, course_name, description, credits, teacher_id
### Enrollments
- id, student_id, course_id, enrollment_date, status, grade
## Security Features
- BCrypt password encryption
- Session-based authentication
- Role-based authorization (@PreAuthorize)
- CSRF protection
- Custom error handling
- Method-level security
## Key Features
### Admin Dashboard
- Create users (any role)
- Enable/Disable accounts
- Delete users
- View all system data
### Teacher Dashboard
- Create/Edit courses
- Delete courses (cascade)
- View enrolled students
- Grade students
### Student Dashboard
- View available courses
- Enroll in courses
- View grades
- Track enrollment status
## Cascade Deletion
When a course is deleted:
- All enrollments are automatically deleted
- Student and teacher accounts remain intact
- Database integrity is maintained
## Docker Commands
### Start application
```bash
docker-compose up --build -d
```
### View logs
```bash
docker-compose logs -f app
```
### Stop application
```bash
docker-compose down
```
### Reset database
```bash
docker-compose down -v
docker-compose up --build
```
## Troubleshooting
### Port already in use
Change port in application.properties or stop conflicting process
### Cannot login
Use default credentials or reset database with: docker-compose down -v
### Database connection failed
Ensure PostgreSQL container is running: docker-compose ps
### View database
```bash
docker exec -it sepm-postgres psql -U myuser -d mydatabase
\dt
SELECT * FROM users;
```
## Testing with Postman
1. Import SEPM_Assignment_API.postman_collection.json
2. Login using /login endpoint to get session cookie
3. Test endpoints with authenticated session
4. Collection includes all REST API endpoints
## Requirements Met
- Authentication & Authorization (Spring Security)
- PostgreSQL Database
- MVC Architecture
- REST API Principles
- Multiple entity relationships (1:N)
- Dockerized application
- Role-based authorization
- Frontend with authentication
## Author
SEPM Assignment Project - February 2026
## License
Educational purposes - SEPM Assignment
