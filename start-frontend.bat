@echo off
chcp 65001 >nul
echo ========================================
echo   启动 Vue Lab 前端开发服务器
echo ========================================
echo.
cd /d "%~dp0vue-lab"
echo 当前目录: %cd%
echo.
echo 正在启动开发服务器...
echo.
npm run dev
pause
