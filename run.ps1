# SEPM Assignment - Build and Run Script
# PowerShell script to build and run the application

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SEPM Assignment - Quick Run Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "Checking Docker..." -ForegroundColor Yellow
docker --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Docker is not installed or not running!" -ForegroundColor Red
    Write-Host "Please install Docker Desktop and try again." -ForegroundColor Red
    exit 1
}
Write-Host "✓ Docker is available" -ForegroundColor Green
Write-Host ""

# Ask user what to do
Write-Host "What would you like to do?" -ForegroundColor Yellow
Write-Host "1. Build and start the application" -ForegroundColor White
Write-Host "2. Stop the application" -ForegroundColor White
Write-Host "3. Stop and remove all data (clean restart)" -ForegroundColor White
Write-Host "4. View application logs" -ForegroundColor White
Write-Host "5. Check container status" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter your choice (1-5)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "Building and starting the application..." -ForegroundColor Cyan
        Write-Host "This may take a few minutes on first run..." -ForegroundColor Yellow
        Write-Host ""

        docker-compose up --build

        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "========================================" -ForegroundColor Green
            Write-Host "  Application is running!" -ForegroundColor Green
            Write-Host "========================================" -ForegroundColor Green
            Write-Host ""
            Write-Host "Access the application at: http://localhost:8080" -ForegroundColor Cyan
            Write-Host ""
            Write-Host "Default credentials:" -ForegroundColor Yellow
            Write-Host "  Admin:   admin / admin123" -ForegroundColor White
            Write-Host "  Teacher: teacher / teacher123" -ForegroundColor White
            Write-Host "  Student: student / student123" -ForegroundColor White
            Write-Host ""
        }
    }
    "2" {
        Write-Host ""
        Write-Host "Stopping the application..." -ForegroundColor Cyan
        docker-compose down
        Write-Host ""
        Write-Host "✓ Application stopped" -ForegroundColor Green
    }
    "3" {
        Write-Host ""
        Write-Host "Stopping and removing all data..." -ForegroundColor Cyan
        docker-compose down -v
        Write-Host ""
        Write-Host "✓ Application stopped and data removed" -ForegroundColor Green
        Write-Host "Note: Default users will be recreated on next startup" -ForegroundColor Yellow
    }
    "4" {
        Write-Host ""
        Write-Host "Showing application logs (Press Ctrl+C to exit)..." -ForegroundColor Cyan
        Write-Host ""
        docker-compose logs -f app
    }
    "5" {
        Write-Host ""
        Write-Host "Container Status:" -ForegroundColor Cyan
        docker-compose ps
        Write-Host ""
    }
    default {
        Write-Host ""
        Write-Host "Invalid choice. Please run the script again." -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "For more information, see README.md or QUICKSTART.md" -ForegroundColor Cyan
Write-Host ""
