@echo off
chcp 65001 >nul
echo ========================================
echo   启动后端 + 前端开发环境
echo ========================================
echo.
echo [1/2] 启动后端 Spring Boot...
start "MyBlog-Backend" cmd /k "cd /d %~dp0MyBlog && mvnw.cmd spring-boot:run"
echo.
timeout /t 3 /nobreak >nul
echo [2/2] 启动前端 Vue...
start "Vue-Lab-Frontend" cmd /k "cd /d %~dp0vue-lab && npm run dev"
echo.
echo ========================================
echo   启动完成！
echo   后端: http://localhost:8080
echo   前端: http://localhost:5173
echo   Swagger: http://localhost:8080/swagger-ui.html
echo ========================================
pause
