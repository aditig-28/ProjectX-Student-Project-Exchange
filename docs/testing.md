# ProjectX - Comprehensive Verification & Test Report

This document records the automated and manual verification results conducted against the live running ProjectX environment, backed by **Apache Tomcat 10.1.60** and a local **Oracle Database 21c XE** instance.

---

## 1. Test Environment Summary
| Component | Detected Specification | Verification Status |
| :--- | :--- | :--- |
| **Operating System** | Windows 11 Home (64-bit) | Verified |
| **Java Development Kit** | OpenJDK 21.0.10 (build 21.0.10+-14961533-b1163.108) | Verified via `javac -version` |
| **Build & Dependency Tool** | Apache Maven 3.9.9 | Verified (`mvn clean package`) |
| **Servlet Web Container** | Apache Tomcat 10.1.60 (Port 8080) | Verified (Catalina Connector Active) |
| **Database Server** | Oracle Database 21c Express Edition Release 21.0.0.0.0 | Verified (`INSTANCE_NAME: xe, STATUS: OPEN`) |
| **Database Pluggable DB** | XEPDB1 (Service on Port 1521) | Verified (Mode: READ WRITE) |
| **Database User Schema** | `PROJECTX` | Verified via SQL*Plus & JDBC |
| **JDBC Driver** | Oracle Thin JDBC Driver (`ojdbc11.jar`, v21.9.0.0.0) | Verified via `DBUtil.testConnection()` |

---

## 2. Test Execution Matrix

| Test ID | Test Category | Scenario / Assertion | HTTP Method & URI | Expected Result | Actual Result | Status |
| :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | Connectivity | Live Oracle 21c Database Health Check | `GET /api/health` | HTTP 200, `"connected": true` | Oracle 21.0.0.0.0 returned with product metadata | **PASSED** |
| **TC-02** | Student Read | Fetch all registered students from Oracle DB | `GET /api/students` | HTTP 200, array of 6 students | Returned 6 seeded students sorted alphabetically | **PASSED** |
| **TC-03** | Student Create | Register a new student profile | `POST /api/students` | HTTP 201 Created with generated studentId | Generated `studentId: 100`, success envelope | **PASSED** |
| **TC-04** | Student Read By ID | Retrieve student by primary key ID | `GET /api/students/100` | HTTP 200, student record | Found "Aditi Gawale" with departmental info | **PASSED** |
| **TC-05** | Validation | Prevent duplicate student email registration | `POST /api/students` (same email) | HTTP 409 Conflict | Blocked with `"Email address already registered"` | **PASSED** |
| **TC-06** | Student Update | Update student technical skills and bio | `PUT /api/students/100` | HTTP 200, updated record | Record modified and persisted in Oracle | **PASSED** |
| **TC-07** | Project Create | Publish project with creator as owner | `POST /api/projects` | HTTP 201 Created with generated projectId | Created `projectId: 200`, owner set to 100 | **PASSED** |
| **TC-08** | Auto-Lead | Verify project creator enrolled as "Project Lead" | `GET /api/projects/200` | `currentMembers: 1`, Role: "Project Lead" | Creator added to `PROJECT_MEMBERS` automatically | **PASSED** |
| **TC-09** | Project Search | Search projects by keyword and domain filter | `GET /api/projects?search=Quantum&category=Cybersecurity` | HTTP 200, filtered project card | Returned project 200 matching keyword and category | **PASSED** |
| **TC-10** | Validation | Prevent project owner from requesting to join own project | `POST /api/requests` (owner ID) | HTTP 400 Bad Request | Blocked: `"Cannot submit join request to own project"` | **PASSED** |
| **TC-11** | Request Submit | Student 1 submits join request to project 200 | `POST /api/requests` | HTTP 201 Created, `status: "Pending"` | Created `requestId: 100`, pending state | **PASSED** |
| **TC-12** | Validation | Prevent duplicate join request for same project/student | `POST /api/requests` (repeated) | HTTP 409 Conflict | Blocked: `"Join request already submitted"` | **PASSED** |
| **TC-13** | Request Workflow | Owner accepts join request | `PUT /api/requests/100` (`status: "Accepted"`) | HTTP 200 OK, `status: "Accepted"` | Status updated to "Accepted", `responded_at` set | **PASSED** |
| **TC-14** | Sync Membership | Verify accepted applicant automatically added to team | `GET /api/projects/200` | `currentMembers: 2`, Role: "Collaborator" | Student 1 enrolled in `PROJECT_MEMBERS` atomically | **PASSED** |
| **TC-15** | Dashboard | Retrieve personalized dashboard metrics for student 1 | `GET /api/dashboard?studentId=1` | HTTP 200, personalized stats | Correct counts for projects, requests, and teams | **PASSED** |
| **TC-16** | Project Delete | Delete project and cascade memberships/requests | `DELETE /api/projects/200` | HTTP 200 OK | Project removed, dependent rows deleted | **PASSED** |
| **TC-17** | Verification | Confirm 404 on deleted project | `GET /api/projects/200` | HTTP 404 Not Found | Returned error envelope with HTTP 404 | **PASSED** |
| **TC-18** | Student Delete | Delete student profile | `DELETE /api/students/100` | HTTP 200 OK | Student removed from Oracle database | **PASSED** |
| **TC-19** | Verification | Confirm 404 on deleted student | `GET /api/students/100` | HTTP 404 Not Found | Returned error envelope with HTTP 404 | **PASSED** |
| **TC-20** | Static Delivery | Verify all 7 HTML pages serve HTTP 200 from Tomcat | `GET /projectx/*.html` | HTTP 200 for all 7 pages | All 7 HTML pages served cleanly | **PASSED** |

---

## 3. Repeatable CLI Verification Command
To re-run the health and sanity check against the live server at any point:
```powershell
curl.exe -i http://localhost:8080/projectx/api/health
```
Expected output:
```json
HTTP/1.1 200
{
  "success": true,
  "message": "System and Oracle Database are fully operational",
  "data": {
    "connected": true,
    "databaseProductVersion": "Oracle Database 21c Express Edition Release 21.0.0.0.0 - Production",
    ...
  }
}
```
