@echo off
cd /d "%~dp0"
title CinePass Tamil Nadu - Movie Ticket Booking App (Oracle 19c)
echo ========================================================
echo   CinePass Tamil Nadu - Oracle Database 19c
echo ========================================================
echo Launching Application...
java -cp "bin;lib/ojdbc8.jar;lib/ojdbc.jar" com.movieticket.Main
if errorlevel 1 (
    echo.
    echo An error occurred while running the application.
    pause
)
