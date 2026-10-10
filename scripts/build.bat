@echo off
REM ============================================================================
REM ProjectX - Maven Build & Packaging Script
REM ============================================================================

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "MVN=%~dp0..\tools\apache-maven-3.9.9\bin\mvn.cmd"
set "TC=%~dp0..\tools\apache-tomcat-10.1.60"

cd /d "%~dp0.."

echo ========================================================================
echo Building ProjectX with Apache Maven 3.9.9...
echo ========================================================================

call "%MVN%" package

if %ERRORLEVEL% equ 0 (
    echo.
    echo Copying target\projectx.war to Tomcat webapps...
    copy /y "target\projectx.war" "%TC%\webapps\projectx.war"
    echo Build and deployment completed successfully!
) else (
    echo [ERROR] Build failed! Check Maven output above.
)
pause
