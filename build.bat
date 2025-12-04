@echo off
echo Building project...
call mvn clean package -q
if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)
echo Build complete!