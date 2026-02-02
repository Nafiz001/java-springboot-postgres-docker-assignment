# Quick Start Guide

## Prerequisites
- Docker Desktop installed and running
- Internet connection for downloading images

## Steps to Run

### 1. Start the Application

Open PowerShell in the project directory and run:

```powershell
docker-compose up --build
```

This command will:
- Build the Spring Boot application
- Start PostgreSQL database
- Start the application on port 8080

### 2. Access the Application

Open your browser and go to: **http://localhost:8080**

### 3. Login with Default Accounts

The system automatically creates three users on first startup:

| Role    | Username | Password    |
|---------|----------|-------------|
| Admin   | admin    | admin123    |
| Teacher | teacher  | teacher123  |
| Student | student  | student123  |

### 4. Try Different Roles

#### As Student:
1. Login with `student` / `student123`
2. View available courses
3. Enroll in courses by clicking "Enroll" button
4. View your enrollments and grades

#### As Teacher:
1. Login with `teacher` / `teacher123`
2. Click "Create New Course" to add courses
3. View enrolled students in your courses
4. Assign grades to students

#### As Admin:
1. Login with `admin` / `admin123`
2. View all users and courses
3. Manage the system
4. Delete users (except yourself)

### 5. Create New Users

Click "Register here" on the login page to create new accounts:
- Choose username, password, email
- Select role (STUDENT, TEACHER, or ADMIN)
- Submit to create account

### 6. Stop the Application

Press `Ctrl+C` in the PowerShell window, then run:

```powershell
docker-compose down
```

To also remove the database data:

```powershell
docker-compose down -v
```

## Troubleshooting

### Docker Desktop Not Running
If you see: `open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified`

**Solution:**
1. Start Docker Desktop from Start Menu
2. Wait until it says "Docker Desktop is running"
3. Try the command again

### Port Already in Use
If you see: `bind: Only one usage of each socket address`

**Solution:** Another application is using the port. The application is now configured to use port **8081** instead of 8080.

If port 8081 is also busy, edit `compose.yaml` and change:
```yaml
ports:
  - "8082:8080"  # Use any available port
```

### Database Connection Issues
Ensure PostgreSQL container is healthy:
```powershell
docker-compose ps
docker-compose logs postgres
```

### Application Won't Start
Check application logs:
```powershell
docker-compose logs app
```

### Clear All Data and Restart
```powershell
docker-compose down -v
docker system prune -a
docker-compose up --build
```

## API Endpoints

All REST API endpoints are available at `http://localhost:8080/api/`

Examples:
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Create course (Teacher/Admin only)
- `POST /api/enrollments` - Enroll student
- `GET /api/users` - Get all users (Admin only)

For detailed API documentation, see README.md

## Next Steps

1. Explore the dashboards for each role
2. Create courses as a teacher
3. Enroll in courses as a student
4. Test the REST APIs using curl or Postman
5. Check the database using PostgreSQL client

## Need Help?

See:
- README.md - Complete documentation
- TESTING.md - Testing scenarios and examples
