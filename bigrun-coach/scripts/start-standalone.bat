@echo off

REM ─────────────────────────────────────────────
REM BigRun Coach — standalone start
REM ─────────────────────────────────────────────
REM Folder structure:
REM   .\bigrun-coach-0.0.1-SNAPSHOT.jar
REM   .\.env
REM ─────────────────────────────────────────────

SET SCRIPT_DIR=%~dp0
SET JAR=%SCRIPT_DIR%bigrun-coach-0.0.1-SNAPSHOT.jar
SET ENV_FILE=%SCRIPT_DIR%.env

IF NOT EXIST "%JAR%" (
    echo ERROR: jar not found: %JAR%
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
echo   Ollama  : %OLLAMA_BASE_URL%
echo.

java -jar "%JAR%" --spring.profiles.active=%SPRING_PROFILES_ACTIVE%
pause
