# ProjectX - REST API Specification

This document details all HTTP endpoints exposed by the ProjectX Java Jakarta Servlet backend deployed at base URL:
`http://localhost:8080/projectx/api`

All responses follow the unified `ApiResponse<T>` JSON envelope:
```json
{
  "success": true,
  "message": "Human-readable status summary",
  "data": { ... },
  "error": null,
  "status": 200,
  "timestamp": 1791653800000
}
```

---

## 1. System Health & Diagnostics

### `GET /api/health`
Checks live Oracle Database connectivity and returns runtime driver and database metadata.

- **Response `200 OK`**:
```json
{
  "success": true,
  "message": "System and Oracle Database are fully operational",
  "data": {
    "connected": true,
    "databaseProductName": "Oracle",
    "databaseProductVersion": "Oracle Database 21c Express Edition Release 21.0.0.0.0 - Production",
    "driverName": "Oracle JDBC driver",
    "driverVersion": "21.9.0.0.0",
    "driverClass": "oracle.jdbc.OracleDriver",
    "jdbcUrl": "jdbc:oracle:thin:@localhost:1521/XEPDB1",
    "dbUser": "PROJECTX"
  },
  "status": 200
}
```

---

## 2. Students API (`/api/students`)

### `GET /api/students`
Retrieves all registered students sorted alphabetically with calculated statistics.
- **Query Parameters**: None
- **Response `200 OK`**: Array of `Student` objects including `projectsCreated`, `teamsJoined`, and `pendingRequestsSent`.

### `GET /api/students/{id}`
Retrieves a specific student by primary key ID.
- **Path Parameters**: `id` (numeric student ID)
- **Response `200 OK`**: Single `Student` object.
- **Response `404 Not Found`**: When student does not exist.

### `POST /api/students`
Registers a new student profile.
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "fullName": "Aditi Gawale",
  "email": "aditi.gawale@college.edu",
  "department": "Computer Science and Engineering",
  "yearOfStudy": "3rd Year",
  "skills": "Java, Oracle, Spring Boot, Web Development",
  "bio": "Full-stack enthusiast building student collaboration tools",
  "avatarUrl": "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80"
}
```
- **Response `201 Created`**: Returns created student with newly generated `studentId`.
- **Response `400 Bad Request`**: Missing required fields (`fullName`, `email`, `department`, `yearOfStudy`).
- **Response `409 Conflict`**: Email already registered in the system.

### `PUT /api/students/{id}`
Updates an existing student's details.
- **Request Body**: Fields to update (`fullName`, `email`, `department`, `yearOfStudy`, `skills`, `bio`, `avatarUrl`).
- **Response `200 OK`**: Updated student object.
- **Response `409 Conflict`**: New email is already claimed by another student.

### `DELETE /api/students/{id}`
Deletes a student record and cascades to owned projects and join requests.
- **Response `200 OK`**: Deletion confirmation.
- **Response `404 Not Found`**: ID does not exist.

---

## 3. Projects API (`/api/projects`)

### `GET /api/projects`
Retrieves projects with optional multi-attribute search and filtering.
- **Query Parameters**:
  - `search` (optional string): Substring search across title, description, and required skills.
  - `category` (optional string): e.g. `Artificial Intelligence`, `IoT & Embedded`, `Cybersecurity`.
  - `status` (optional string): `Open`, `In Progress`, `Completed`.
  - `tech` (optional string): Technology filter (e.g. `Java`, `Python`).
  - `ownerId` (optional number): Filter projects created by a specific student.
- **Response `200 OK`**: Array of project cards with joined owner details and spots remaining.

### `GET /api/projects/{id}`
Retrieves full project details including collaborating team members list.
- **Response `200 OK`**:
```json
{
  "success": true,
  "data": {
    "projectId": 101,
    "title": "EcoTrack: Campus Energy & Carbon Footprint Monitor",
    "description": "An IoT-powered telemetry platform collecting live power consumption metrics...",
    "category": "IoT & Embedded",
    "technologies": "Embedded C, MQTT, Java, Oracle, Chart.js",
    "ownerId": 3,
    "teamSize": 4,
    "requiredSkills": "Embedded Systems, MQTT, Backend Java, UI Dashboards",
    "status": "Open",
    "githubUrl": "https://github.com/campus-ecotrack",
    "ownerName": "Rohan Mehta",
    "ownerEmail": "rohan.mehta@college.edu",
    "ownerDepartment": "Electronics & Telecommunication",
    "currentMembers": 1,
    "spotsRemaining": 3,
    "members": [
      {
        "memberId": 1,
        "studentId": 3,
        "studentName": "Rohan Mehta",
        "role": "Project Lead"
      }
    ]
  }
}
```

### `POST /api/projects`
Publishes a new project and automatically enrolls the creator as "Project Lead".
- **Request Body**:
```json
{
  "title": "QuantumKey: Post-Quantum Cryptographic Key Exchange",
  "description": "Implementation of lattice-based post-quantum cryptography algorithms...",
  "category": "Cybersecurity",
  "technologies": "Java, Bouncy Castle, REST, Oracle 21c",
  "requiredSkills": "Applied Cryptography, Java, Network Protocols",
  "ownerId": 1,
  "teamSize": 4,
  "status": "Open",
  "githubUrl": "https://github.com/campus/quantum-key"
}
```
- **Response `201 Created`**: Returns new project with generated `projectId`.

### `PUT /api/projects/{id}`
Updates existing project details.
- **Response `200 OK`**: Updated project record.

### `DELETE /api/projects/{id}`
Permanently deletes a project, cascading removal to `PROJECT_MEMBERS` and `JOIN_REQUESTS`.
- **Response `200 OK`**: Deletion confirmation.

---

## 4. Join Requests API (`/api/requests`)

### `GET /api/requests`
Retrieves join requests filtered by role.
- **Query Parameters**:
  - `ownerId`: Returns applications submitted to projects owned by this student.
  - `studentId`: Returns applications submitted by this student.
  - `projectId`: Returns applications submitted to a specific project.
- **Response `200 OK`**: Array of request items.

### `POST /api/requests`
Submits a new request from a student to join an open project.
- **Request Body**:
```json
{
  "projectId": 101,
  "studentId": 1,
  "message": "Hi Rohan, I have experience setting up Java REST pipelines for telemetry ingestion."
}
```
- **Business Rule Validations**:
  - `400 Bad Request`: Student cannot apply to their own project.
  - `400 Bad Request`: Cannot apply to a project marked as `Completed`.
  - `409 Conflict`: Cannot apply if an active request already exists for this project/student pair.
- **Response `201 Created`**: Created request with status `Pending`.

### `PUT /api/requests/{id}`
Processes a join request by updating its status to `Accepted` or `Rejected`.
- **Request Body**:
```json
{
  "status": "Accepted"
}
```
- **Transactional Behavior**:
  - If status is `Accepted`, the applicant is automatically added to `PROJECT_MEMBERS` with the role `Collaborator`.
  - Sets `responded_at` to the current timestamp.
- **Response `200 OK`**: Updated request details.

---

## 5. Dashboard API (`/api/dashboard`)

### `GET /api/dashboard?studentId={id}`
Returns platform metrics and personalized user data for the active demo student.
- **Response `200 OK`**:
```json
{
  "success": true,
  "data": {
    "totalStudents": 6,
    "totalProjects": 6,
    "openProjects": 4,
    "activeCollaborations": 5,
    "totalRequests": 6,
    "currentStudentId": 1,
    "myProjectsCount": 1,
    "myMembershipsCount": 1,
    "incomingRequestsCount": 2,
    "outgoingRequestsCount": 1,
    "myProjects": [ ... ],
    "incomingRequests": [ ... ],
    "outgoingRequests": [ ... ],
    "joinedProjects": [ ... ],
    "categoryDistribution": {
      "IoT & Embedded": 2,
      "Artificial Intelligence": 1,
      "Web Development": 1,
      "Cybersecurity": 1,
      "Data Science": 1
    }
  }
}
```
