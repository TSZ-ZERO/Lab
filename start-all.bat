@echo off
chcp 65001 >nul
echo ========================================
echo   Start Backend + Frontend
echo ========================================
echo.
echo [1/2] Starting Backend Spring Boot...
start "MyBlog-Backend" cmd /k "cd /d "%~dp0MyBlog" && mvnw.cmd spring-boot:run"
echo.
timeout /t 3 /nobreak >nul
echo [2/2] Starting Frontend Vue...
start "Vue-Lab-Frontend" cmd /k "cd /d "%~dp0vue-lab" && npm run dev"
echo.
echo ========================================
echo   Started!
echo   Backend: http://localhost:8080
echo   Frontend: http://localhost:5173
echo   Swagger: http://localhost:8080/swagger-ui.html
echo ========================================
pause
