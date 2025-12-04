@echo off
if not exist target\carekeeperaquarium-1.0-SNAPSHOT.jar (
    echo JAR not found! Run build.bat first.
    pause
    exit /b 1
)
echo Starting Aquarium Server...
start "Aquarium Server" cmd /k "java -Dorg.jline.terminal.type=windows -jar target\carekeeperaquarium-1.0-SNAPSHOT.jar"