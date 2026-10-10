@echo off
REM ============================================================================
REM ProjectX - Oracle Database Initializer Script
REM Executes schema.sql and sample_data.sql into Oracle Database 21c XE
REM ============================================================================

set "SQLPLUS=C:\app\oracle\dbhomeXE\bin\sqlplus.exe"

if not exist "%SQLPLUS%" (
    echo [ERROR] SQL*Plus executable not found at %SQLPLUS%
    echo Please ensure Oracle Database 21c XE is installed.
    pause
    exit /b 1
)

echo ========================================================================
echo Initializing ProjectX Database Schema & Realistic Sample Data...
echo ========================================================================

cd /d "%~dp0.."

(
echo SET DEFINE OFF;
echo @database\schema.sql
echo @database\sample_data.sql
echo SELECT table_name FROM user_tables;
echo SELECT 'STUDENTS' as tbl, count(*) as cnt FROM students
echo UNION ALL
echo SELECT 'PROJECTS', count(*) FROM projects
echo UNION ALL
echo SELECT 'PROJECT_MEMBERS', count(*) FROM project_members
echo UNION ALL
echo SELECT 'JOIN_REQUESTS', count(*) FROM join_requests;
echo exit;
) | "%SQLPLUS%" -S PROJECTX/projectx123@localhost:1521/XEPDB1

echo.
echo Database setup finished successfully!
pause
