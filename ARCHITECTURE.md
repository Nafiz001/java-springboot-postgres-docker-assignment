# System Architecture

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       Web Browser                            │
│                    (http://localhost:8081)                   │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ HTTP/HTTPS
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  Spring Boot Application                     │
│                      (Port 8080)                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │            Presentation Layer                         │  │
│  │  - Thymeleaf Templates                               │  │
│  │  - MVC Controllers                                   │  │
│  │  - REST API Controllers                              │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │            Security Layer                             │  │
│  │  - Spring Security                                   │  │
│  │  - Authentication & Authorization                    │  │
│  │  - Role-Based Access Control                         │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │            Service Layer                              │  │
│  │  - UserService                                       │  │
│  │  - CourseService                                     │  │
│  │  - EnrollmentService                                 │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │            Repository Layer                           │  │
│  │  - UserRepository                                    │  │
│  │  - CourseRepository                                  │  │
│  │  - EnrollmentRepository                              │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ JDBC
                      │
┌─────────────────────▼───────────────────────────────────────┐
│              PostgreSQL Database                             │
│                  (Port 5432)                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Tables:                                              │  │
│  │  - users (id, username, password, role, ...)        │  │
│  │  - courses (id, course_code, name, teacher_id, ...) │  │
│  │  - enrollments (id, student_id, course_id, ...)     │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## MVC Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         MODEL                                │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐           │
│  │   User     │  │   Course   │  │ Enrollment │           │
│  │  Entity    │  │   Entity   │  │   Entity   │           │
│  └────────────┘  └────────────┘  └────────────┘           │
│  - Represent database tables                                │
│  - JPA annotations                                          │
│  - Relationships (One-to-Many, Many-to-One)                │
└─────────────────────────────────────────────────────────────┘
                             ▲
                             │
┌────────────────────────────┼────────────────────────────────┐
│                         CONTROLLER                           │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │  MVC Controllers │  │  REST Controllers │                │
│  │  - Auth          │  │  - /api/auth     │                │
│  │  - Dashboard     │  │  - /api/users    │                │
│  │  - Course        │  │  - /api/courses  │                │
│  │                  │  │  - /api/enrollments               │
│  └──────────────────┘  └──────────────────┘                │
│  - Handle HTTP requests                                     │
│  - Call services                                            │
│  - Return views or JSON                                     │
└─────────────────────────────────────────────────────────────┘
                             ▲
                             │
┌────────────────────────────┼────────────────────────────────┐
│                           VIEW                               │
│  ┌──────────────────────────────────────────┐              │
│  │       Thymeleaf Templates                 │              │
│  │  - login.html                            │              │
│  │  - register.html                         │              │
│  │  - student-dashboard.html                │              │
│  │  - teacher-dashboard.html                │              │
│  │  - admin-dashboard.html                  │              │
│  │  - course-form.html                      │              │
│  │  - course-detail.html                    │              │
│  │  - courses.html                          │              │
│  └──────────────────────────────────────────┘              │
│  - Render HTML pages                                        │
│  - Display data to users                                    │
│  - Handle user input                                        │
└─────────────────────────────────────────────────────────────┘
```

## Database Entity Relationships

```
┌─────────────────────────┐
│         User            │
│─────────────────────────│
│ id (PK)                 │
│ username                │
│ password                │
│ email                   │
│ full_name               │
│ role                    │
│ enabled                 │
└─────────┬───────────────┘
          │
          │ One-to-Many
          │ (as Teacher)
          │
          ▼
┌─────────────────────────┐
│        Course           │
│─────────────────────────│
│ id (PK)                 │
│ course_code             │
│ course_name             │
│ description             │
│ credits                 │
│ teacher_id (FK)         │◄──────┐
└─────────┬───────────────┘       │
          │                        │
          │ One-to-Many            │
          │                        │
          ▼                        │
┌─────────────────────────┐       │
│      Enrollment         │       │
│─────────────────────────│       │
│ id (PK)                 │       │
│ student_id (FK)         │───────┼──────┐
│ course_id (FK)          │───────┘      │
│ enrollment_date         │              │
│ status                  │              │
│ grade                   │              │
└─────────────────────────┘              │
                                         │
                                         │ Many-to-One
                                         │ (as Student)
                                         │
                                         ▼
                              ┌─────────────────────────┐
                              │         User            │
                              │  (Student Role)         │
                              └─────────────────────────┘
```

## Security Flow

```
┌──────────────┐
│   Browser    │
└──────┬───────┘
       │
       │ 1. Request /dashboard
       ▼
┌──────────────────────┐
│  Security Filter     │
│   Chain              │
└──────┬───────────────┘
       │
       │ 2. Check Authentication
       ▼
┌──────────────────────┐      ┌──────────────────────┐
│  Not Authenticated?  │─Yes──►  Redirect to /login  │
└──────┬───────────────┘      └──────────────────────┘
       │ No (Authenticated)
       │
       │ 3. Check Authorization
       ▼
┌──────────────────────┐      ┌──────────────────────┐
│   Has Required       │─No───►   Access Denied      │
│   Role?              │      │   (403 Error)        │
└──────┬───────────────┘      └──────────────────────┘
       │ Yes
       │
       │ 4. Allow Request
       ▼
┌──────────────────────┐
│    Controller        │
└──────┬───────────────┘
       │
       │ 5. Process & Return
       ▼
┌──────────────────────┐
│    Response          │
└──────────────────────┘
```

## REST API Structure

```
/api
├── /auth
│   └── POST /register          # Register new user
│
├── /users                       # Admin only
│   ├── GET /                   # Get all users
│   ├── GET /{id}               # Get user by ID
│   ├── GET /role/{role}        # Get users by role
│   └── DELETE /{id}            # Delete user
│
├── /courses                     # All authenticated users
│   ├── GET /                   # Get all courses
│   ├── GET /{id}               # Get course by ID
│   ├── GET /teacher/{id}       # Get courses by teacher
│   ├── POST /                  # Create course (Teacher/Admin)
│   ├── PUT /{id}               # Update course (Teacher/Admin)
│   └── DELETE /{id}            # Delete course (Teacher/Admin)
│
└── /enrollments
    ├── POST /                  # Enroll student (Student/Admin)
    ├── GET /student/{id}       # Get student enrollments
    ├── GET /course/{id}        # Get course enrollments (Teacher/Admin)
    ├── PUT /{id}/grade         # Update grade (Teacher/Admin)
    ├── PUT /{id}/status        # Update status (Teacher/Admin)
    └── DELETE /{id}            # Delete enrollment (Admin)
```

## Docker Container Architecture

```
┌─────────────────────────────────────────────────────────┐
│               Docker Compose Network                     │
│                    (sepm-network)                        │
│                                                          │
│  ┌────────────────────────┐  ┌────────────────────────┐│
│  │   App Container        │  │  PostgreSQL Container  ││
│  │   (sepm-app)           │  │  (sepm-postgres)       ││
│  │                        │  │                        ││
│  │  Spring Boot App       │  │  PostgreSQL 15         ││
│  │  Port: 8080            │  │  Port: 5432            ││
│  │                        │  │                        ││
│  │  Depends on ────────────►│  Health Check          ││
│  │  PostgreSQL            │  │                        ││
│  └────────────────────────┘  └───────┬────────────────┘│
│             ▲                         │                 │
│             │                         ▼                 │
│    Host Port: 8080          ┌────────────────────────┐ │
│                             │   Volume               │ │
│                             │   (postgres_data)      │ │
│                             │   Persistent Storage   │ │
│                             └────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Role-Based Access Control

```
┌─────────────────────────────────────────────────────────┐
│                    ADMIN ROLE                            │
│  ✓ Full system access                                   │
│  ✓ Manage users (create, view, delete)                  │
│  ✓ Manage courses (all operations)                      │
│  ✓ View all enrollments                                 │
│  ✓ Assign grades                                        │
│  ✓ System statistics                                    │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                   TEACHER ROLE                           │
│  ✓ Create courses                                       │
│  ✓ Edit own courses                                     │
│  ✓ Delete own courses                                   │
│  ✓ View enrolled students                               │
│  ✓ Assign grades to students                            │
│  ✓ View course statistics                               │
│  ✗ Cannot manage users                                  │
│  ✗ Cannot delete other teachers' courses                │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                   STUDENT ROLE                           │
│  ✓ View all courses                                     │
│  ✓ Enroll in courses                                    │
│  ✓ View own enrollments                                 │
│  ✓ View own grades                                      │
│  ✗ Cannot create courses                                │
│  ✗ Cannot modify courses                                │
│  ✗ Cannot view other students' data                     │
│  ✗ Cannot manage users                                  │
└─────────────────────────────────────────────────────────┘
```

## Request Flow Example: Student Enrolls in Course

```
1. Student clicks "Enroll" button
   └─► POST /api/enrollments
       Body: { studentId: 3, courseId: 1 }

2. EnrollmentRestController receives request
   └─► @PreAuthorize checks if user has STUDENT or ADMIN role
       └─► If no → 403 Forbidden
       └─► If yes → Continue

3. EnrollmentService.enrollStudent()
   └─► Check if already enrolled (prevent duplicates)
   └─► Validate student exists and has STUDENT role
   └─► Validate course exists
   └─► Create new Enrollment entity
   └─► Save to database via EnrollmentRepository

4. Convert Enrollment to EnrollmentDTO

5. Return HTTP 201 Created with EnrollmentDTO

6. Frontend updates to show new enrollment
```

## Technology Stack Layers

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  Thymeleaf, HTML5, CSS3                                 │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                   Application Layer                      │
│  Spring Boot, Spring MVC, Spring Security               │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                    Business Layer                        │
│  Service Classes, Business Logic, Validation            │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                   Persistence Layer                      │
│  JPA/Hibernate, Repository Pattern                      │
└─────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                     Database Layer                       │
│  PostgreSQL 15                                          │
└─────────────────────────────────────────────────────────┘
```

---

This architecture ensures:
- **Separation of Concerns**: Each layer has specific responsibilities
- **Security**: Authentication and authorization at every level
- **Scalability**: Stateless REST APIs, containerized deployment
- **Maintainability**: Clean code structure, clear patterns
- **Testability**: Layered architecture allows easy unit testing
