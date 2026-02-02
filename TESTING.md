# Testing Guide for SEPM Assignment

## Testing with cURL (PowerShell)

### 1. Register Users

**Register a Student:**
```powershell
$body = @{
    username = "alice"
    password = "password123"
    email = "alice@example.com"
    fullName = "Alice Johnson"
    role = "STUDENT"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

**Register a Teacher:**
```powershell
$body = @{
    username = "bob"
    password = "password123"
    email = "bob@example.com"
    fullName = "Bob Williams"
    role = "TEACHER"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

### 2. Login via Browser
- Go to http://localhost:8080/login
- Use default credentials or registered users

### 3. Test API Endpoints (After Login)

**Get All Courses:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/courses" -Method GET
```

**Create a Course (as Teacher):**
```powershell
$courseBody = @{
    courseCode = "CS101"
    courseName = "Introduction to Programming"
    description = "Learn basic programming concepts"
    credits = 3
    teacherId = 2
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/courses" `
    -Method POST `
    -ContentType "application/json" `
    -Body $courseBody
```

**Enroll Student:**
```powershell
$enrollBody = @{
    studentId = 3
    courseId = 1
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/enrollments" `
    -Method POST `
    -ContentType "application/json" `
    -Body $enrollBody
```

## Testing via Web Browser

### 1. Login as Student (student/student123)
- View available courses
- Enroll in courses
- View your enrollments
- Check grades

### 2. Login as Teacher (teacher/teacher123)
- Create new courses
- View your courses
- See enrolled students
- Assign grades

### 3. Login as Admin (admin/admin123)
- View all users
- View all courses
- Manage system
- Delete users/courses

## Manual Testing Scenarios

### Scenario 1: Student Enrollment Flow
1. Login as student
2. Browse available courses
3. Enroll in a course
4. Verify enrollment appears in "My Enrollments"

### Scenario 2: Teacher Course Management
1. Login as teacher
2. Create a new course
3. View enrolled students
4. Assign grades to students

### Scenario 3: Admin Management
1. Login as admin
2. View all users
3. View all courses
4. Test user deletion (except self)

## Database Verification

**Connect to PostgreSQL:**
```powershell
docker-compose exec postgres psql -U myuser -d mydatabase
```

**Useful SQL Queries:**
```sql
-- View all users
SELECT id, username, full_name, role FROM users;

-- View all courses
SELECT id, course_code, course_name, credits FROM courses;

-- View all enrollments
SELECT e.id, u.full_name as student, c.course_name, e.status, e.grade 
FROM enrollments e 
JOIN users u ON e.student_id = u.id 
JOIN courses c ON e.course_id = c.id;

-- Check relationships
SELECT c.course_name, u.full_name as teacher 
FROM courses c 
LEFT JOIN users u ON c.teacher_id = u.id;
```

## Expected Results

### After Initial Setup:
- 3 default users created (admin, teacher, student)
- Database tables created automatically
- All endpoints accessible with proper authentication

### Role-Based Access:
- Students can only enroll and view
- Teachers can create/edit courses and assign grades
- Admins have full access to all features

## Common Issues and Solutions

### Issue: Cannot connect to database
**Solution:** Ensure PostgreSQL container is running
```powershell
docker-compose ps
docker-compose up postgres -d
```

### Issue: Authentication errors
**Solution:** Clear browser cookies or use incognito mode

### Issue: Port 8080 already in use
**Solution:** Change port in compose.yaml or stop other services
```powershell
netstat -ano | findstr :8080
```

### Issue: Build fails
**Solution:** Clean and rebuild
```powershell
./mvnw clean install -DskipTests
```

## Performance Testing

### Load Test with PowerShell
```powershell
1..100 | ForEach-Object -Parallel {
    Invoke-WebRequest -Uri "http://localhost:8081/api/courses" -Method GET
}
```

## Security Testing

### Test Role-Based Access:
1. Try accessing admin endpoints as student (should fail)
2. Try creating courses as student (should fail)
3. Try viewing other user's data (should fail)

### Test CSRF Protection:
- Web forms should work (CSRF enabled)
- API endpoints should work (CSRF disabled for /api/**)

## Monitoring

**View Application Logs:**
```powershell
docker-compose logs -f app
```

**View Database Logs:**
```powershell
docker-compose logs -f postgres
```

**Check Container Status:**
```powershell
docker-compose ps
```

**Check Container Resources:**
```powershell
docker stats
```
