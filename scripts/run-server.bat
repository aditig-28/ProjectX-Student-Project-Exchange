@echo off
REM ============================================================================
REM ProjectX - Tomcat Server Runner Script
REM ============================================================================

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "CATALINA_HOME=%~dp0..\tools\apache-tomcat-10.1.60"

echo ========================================================================
echo Starting ProjectX Portal on Apache Tomcat 10.1...
echo JAVA_HOME: %JAVA_HOME%
echo CATALINA_HOME: %CATALINA_HOME%
echo ========================================================================
echo.
echo Application will be available at:
echo http://localhost:8080/projectx/
echo.

call "%CATALINA_HOME%\bin\catalina.bat" run
