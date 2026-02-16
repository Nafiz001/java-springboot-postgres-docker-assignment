# 🧪 Testing & Git Workflow Guide

## 📋 Table of Contents
1. [Testing Strategy Overview](#testing-strategy-overview)
2. [Test Structure](#test-structure)
3. [Running Tests](#running-tests)
4. [Git Workflow](#git-workflow)
5. [Branch Protection Setup](#branch-protection-setup)
6. [Pull Request Workflow](#pull-request-workflow)
7. [Merge Conflict Resolution](#merge-conflict-resolution)
8. [CI/CD Pipeline](#cicd-pipeline)
9. [Best Practices](#best-practices)

---

## 🎯 Testing Strategy Overview

This project implements enterprise-level testing following industry best practices:

### Test Pyramid
```
           /\
          /  \    E2E Tests (Minimal)
         /____\
        /      \   Integration Tests
       /________\
      /          \  Unit Tests
     /____________\
```

### Test Coverage
- ✅ **Unit Tests**: Services layer (UserService, CourseService)
- ✅ **Integration Tests**: Controllers (AdminController, CourseController)
- ✅ **Repository Tests**: Data access layer (UserRepository, CourseRepository)
- ✅ **Entity Tests**: Domain models (User, Course)

---

## 📁 Test Structure

```
src/test/
├── java/com/example/sepm_assignment/
│   ├── controller/
│   │   ├── AdminControllerTest.java        # Integration tests
│   │   └── CourseControllerTest.java       # Integration tests
│   ├── service/
│   │   ├── UserServiceTest.java            # Unit tests
│   │   └── CourseServiceTest.java          # Unit tests
│   ├── repository/
│   │   ├── UserRepositoryTest.java         # Repository tests
│   │   └── CourseRepositoryTest.java       # Repository tests
│   └── model/
│       ├── UserTest.java                   # Entity tests
│       └── CourseTest.java                 # Entity tests
└── resources/
    └── application-test.yml                # Test configuration
```

---

## 🚀 Running Tests

### Local Testing (Maven)

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run specific test method
mvn test -Dtest=UserServiceTest#registerUser_Success

# Run tests by pattern
mvn test -Dtest="*ServiceTest"

# Run with coverage
mvn clean verify

# Generate test report
mvn surefire-report:report
```

### Test Categories

```bash
# Unit Tests only
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Integration Tests only
mvn test -Dtest="*ControllerTest" -DfailIfNoTests=false

# Repository Tests only
mvn test -Dtest="*RepositoryTest" -DfailIfNoTests=false
```

### Using Docker (Recommended for CI)

The CI pipeline uses Docker with PostgreSQL service for testing:

```yaml
services:
  postgres:
    image: postgres:15-alpine
    env:
      POSTGRES_DB: sepm_test
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
```

---

## 🔄 Git Workflow

### 1. Create Testing Branch

```bash
# Create and switch to testing branch
git checkout -b testing/unit-integration-tests

# Or switch to existing branch
git checkout testing/unit-integration-tests
```

### 2. Make Changes & Test

```bash
# Make your changes
# Run tests locally
mvn clean test

# Check status
git status
```

### 3. Commit with Conventional Commits

```bash
# Stage changes
git add .

# Commit with conventional format
git commit -m "test: add unit tests for UserService"
```

**Conventional Commit Types:**
- `test:` - Adding or updating tests
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `chore:` - Maintenance tasks

### 4. Push to Remote

```bash
# Push to remote branch
git push origin testing/unit-integration-tests

# First time push with upstream
git push -u origin testing/unit-integration-tests
```

---

## 🛡️ Branch Protection Setup

### Step-by-Step Configuration (GitHub UI)

1. **Navigate to Settings**
   ```
   Go to: https://github.com/Nafiz001/java-springboot-postgres-docker-assignment/settings/branches
   ```

2. **Add Branch Protection Rule**
   - Click **"Add rule"** or **"Add branch protection rule"**
   - Branch name pattern: `main`

3. **Configure Protection Rules**

   ✅ **Require a pull request before merging**
   - Enable this option
   - Set required approvals: `1`
   - ✅ Dismiss stale pull request approvals when new commits are pushed
   - ✅ Require review from Code Owners (optional)

   ✅ **Require status checks to pass before merging**
   - Enable this option
   - ✅ Require branches to be up to date before merging
   - Add status checks:
     - `test` (from GitHub Actions)
     - `docker-build` (from GitHub Actions)

   ✅ **Require conversation resolution before merging**
   - Enable this option

   ✅ **Do not allow bypassing the above settings**
   - Enable this option
   - Include administrators: ✅ (Recommended)

   ⛔ **Restrict who can push to matching branches**
   - Optional: Only allow specific users/teams

4. **Save Changes**
   - Click **"Create"** or **"Save changes"**

### Visual Confirmation

After setup, you should see:
```
✓ Require pull request reviews before merging (1 approval)
✓ Require status checks to pass before merging
✓ Require branches to be up to date before merging
✓ Required checks: test, docker-build
✓ Do not allow bypassing the above settings
```

---

## 📝 Pull Request Workflow

### 1. Create Pull Request

After pushing your branch:

```bash
# GitHub will show a link to create PR:
# https://github.com/Nafiz001/java-springboot-postgres-docker-assignment/pull/new/testing/unit-integration-tests
```

**Or manually:**
1. Go to repository on GitHub
2. Click **"Pull requests"** → **"New pull request"**
3. Base: `main` ← Compare: `testing/unit-integration-tests`
4. Click **"Create pull request"**

### 2. Fill PR Template

```markdown
## 🎯 Purpose
Add comprehensive unit and integration tests for the application

## 📋 Changes
- ✅ Unit tests for UserService and CourseService
- ✅ Integration tests for AdminController and CourseController
- ✅ Repository tests with H2 in-memory database
- ✅ Entity tests for domain models
- ✅ GitHub Actions CI workflow with Docker

## 🧪 Testing
- All tests passing (60+ test cases)
- Code coverage: 80%+

## 📸 Screenshots
[Add test results screenshot]

## ✅ Checklist
- [x] Tests added/updated
- [x] All tests passing
- [x] Conventional commits used
- [x] Documentation updated
```

### 3. Wait for CI Checks

The PR will automatically trigger CI workflow:
```
✓ test — Tests passed
✓ docker-build — Docker build successful
```

### 4. Request Review

- Assign reviewer (repository owner)
- Wait for approval
- Address review comments if any

### 5. Merge PR

Once approved and all checks pass:
1. Click **"Merge pull request"**
2. Choose merge strategy:
   - **Merge commit** (default, preserves history)
   - **Squash and merge** (cleaner history)
   - **Rebase and merge** (linear history)
3. Click **"Confirm merge"**
4. Delete branch (optional)

---

## ⚔️ Merge Conflict Resolution

### Scenario: Intentional Conflict

**Creating a conflict for demonstration:**

```bash
# On main branch
git checkout main
echo "Line from main" >> README.md
git add README.md
git commit -m "docs: update README from main"
git push origin main

# On testing branch
git checkout testing/unit-integration-tests
echo "Line from testing" >> README.md
git add README.md
git commit -m "docs: update README from testing"
git push origin testing/unit-integration-tests

# Create PR → Conflict appears!
```

### Resolution Method 1: Local (Recommended)

```bash
# 1. Switch to testing branch
git checkout testing/unit-integration-tests

# 2. Fetch latest changes
git fetch origin

# 3. Merge main into testing branch
git merge origin/main

# 4. Git will show conflict
# CONFLICT (content): Merge conflict in README.md

# 5. Open conflicted file
# You'll see:
<<<<<<< HEAD
Line from testing
=======
Line from main
>>>>>>> origin/main

# 6. Edit to resolve:
Line from main
Line from testing

# 7. Stage resolved file
git add README.md

# 8. Commit merge
git commit -m "merge: resolve conflict in README.md"

# 9. Push
git push origin testing/unit-integration-tests
```

### Resolution Method 2: GitHub UI

1. In PR, click **"Resolve conflicts"**
2. Edit the file directly in browser
3. Remove conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`)
4. Click **"Mark as resolved"**
5. Click **"Commit merge"**

### Best Practices for Conflicts

- ✅ **Communicate**: Discuss with team before resolving
- ✅ **Test after resolution**: Run tests to ensure nothing broke
- ✅ **Prefer rebase for clean history** (advanced):
  ```bash
  git rebase origin/main
  # Resolve conflicts during rebase
  git rebase --continue
  git push --force-with-lease
  ```
- ⚠️ **Never force push to main**

---

## 🔧 CI/CD Pipeline

### GitHub Actions Workflow

**File**: `.github/workflows/test.yml`

```yaml
on:
  push:
    branches: [ main, testing/** ]
  pull_request:
    branches: [ main ]
```

### Pipeline Stages

```
┌─────────────────────────────────────────┐
│ 1. Trigger (on push/PR)                 │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ 2. Setup Environment                    │
│    - Ubuntu runner                      │
│    - JDK 17                             │
│    - PostgreSQL service                 │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ 3. Build                                │
│    mvn clean compile                    │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ 4. Run Tests                            │
│    - Unit tests                         │
│    - Integration tests                  │
│    - All tests                          │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ 5. Generate Reports                     │
│    - Test results                       │
│    - Coverage report                    │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ 6. Docker Build (if tests pass)         │
│    - Build image                        │
│    - Validate docker-compose            │
└─────────────────────────────────────────┘
```

### Status Checks

These checks MUST pass before merge:
- ✅ `test` - All tests passing
- ✅ `docker-build` - Docker build successful

### Viewing Results

1. Go to **Actions** tab in GitHub
2. Click on workflow run
3. View test results, logs, and artifacts

---

## ✨ Best Practices

### Testing Best Practices

1. **AAA Pattern** (Arrange-Act-Assert)
   ```java
   @Test
   void testMethod() {
       // Arrange
       User user = new User();
       user.setUsername("test");
       
       // Act
       UserDTO result = service.registerUser(user);
       
       // Assert
       assertNotNull(result);
   }
   ```

2. **Descriptive Test Names**
   ```java
   @Test
   @DisplayName("Should throw exception when username already exists")
   void registerUser_UsernameExists_ThrowsException() { }
   ```

3. **Test One Thing**
   - Each test should verify one specific behavior
   - Don't test multiple scenarios in one test

4. **Use Mocks for Unit Tests**
   ```java
   @Mock
   private UserRepository userRepository;
   
   @InjectMocks
   private UserService userService;
   ```

5. **Clean Test Data**
   ```java
   @BeforeEach
   void setUp() {
       userRepository.deleteAll();
   }
   ```

### Git Best Practices

1. **Commit Often**
   - Small, focused commits
   - Each commit should be deployable

2. **Write Clear Commit Messages**
   ```
   test: add unit tests for UserService
   
   - Test user registration
   - Test duplicate username validation
   - Test user retrieval by ID
   ```

3. **Pull Before Push**
   ```bash
   git pull origin testing/unit-integration-tests
   git push origin testing/unit-integration-tests
   ```

4. **Keep Branches Up-to-Date**
   ```bash
   git checkout testing/unit-integration-tests
   git merge main
   ```

5. **Delete Merged Branches**
   ```bash
   git branch -d testing/unit-integration-tests
   git push origin --delete testing/unit-integration-tests
   ```

### Code Review Best Practices

1. **As Author**:
   - Self-review before requesting review
   - Provide context in PR description
   - Respond to comments promptly

2. **As Reviewer**:
   - Be constructive and respectful
   - Check tests thoroughly
   - Verify CI passes
   - Test locally if needed

---

## 📊 Test Results

### Expected Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.sepm_assignment.service.UserServiceTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.service.CourseServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.controller.AdminControllerTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.controller.CourseControllerTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.repository.UserRepositoryTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.repository.CourseRepositoryTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.model.UserTest
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.sepm_assignment.model.CourseTest
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 98, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🎓 Summary

This project demonstrates:
- ✅ Professional testing strategy
- ✅ Clean architecture with separation of concerns
- ✅ Industry-standard Git workflow
- ✅ Automated CI/CD pipeline
- ✅ Branch protection and code review process
- ✅ Comprehensive documentation

**Next Steps:**
1. Configure branch protection on GitHub
2. Create your first pull request
3. Review and merge the testing branch
4. Continue with TDD (Test-Driven Development) for new features

---

## 📞 Support

For questions or issues:
1. Check GitHub Issues
2. Review test documentation
3. Contact repository maintainer

**Happy Testing! 🚀**
