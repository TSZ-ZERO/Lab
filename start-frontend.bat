@echo off
chcp 65001 >nul
echo ========================================
echo   Start Vue Lab Frontend
echo ========================================
echo.
cd /d "%~dp0vue-lab"
echo Current directory: %cd%
echo.
echo Starting dev server...
echo.
npm run dev
pause
