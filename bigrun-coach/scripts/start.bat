@echo off

REM ─────────────────────────────────────────────
REM BigRun Coach — start from jar
REM ─────────────────────────────────────────────
REM Requirements:
REM   - PostgreSQL running on localhost:5432
REM   - Ollama running on localhost:11434
REM   - .env file in bigrun-coach/ directory
REM   - jar built: mvn clean package -DskipTests
REM ─────────────────────────────────────────────

SET SCRIPT_DIR=%~dp0
SET PROJECT_DIR=%SCRIPT_DIR%..
SET JAR=%PROJECT_DIR%\target\bigrun-coach-0.0.1-SNAPSHOT.jar
SET ENV_FILE=%PROJECT_DIR%\.env

IF NOT EXIST "%JAR%" (
    echo ERROR: jar not found: %JAR%
    echo Run: mvn clean package -DskipTests
    pause
    exit /b 1
)

IF NOT EXIST "%ENV_FILE%" (
    echo ERROR: .env not found: %ENV_FILE%
    pause
    exit /b 1
)

REM Load .env
FOR /F "tokens=1,* delims==" %%A IN (%ENV_FILE%) DO (
    IF NOT "%%A"=="" IF NOT "%%A:~0,1%"=="#" SET %%A=%%B
)

IF "%SPRING_PROFILES_ACTIVE%"=="" SET SPRING_PROFILES_ACTIVE=dev

echo Starting BigRun Coach...
echo   Profile : %SPRING_PROFILES_ACTIVE%
echo   Jar     : %JAR%
echo.

java -jar "%JAR%" --spring.profiles.active=%SPRING_PROFILES_ACTIVE%
pause
