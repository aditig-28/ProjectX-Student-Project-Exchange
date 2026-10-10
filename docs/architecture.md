# ProjectX - System Architecture & Engineering Design

## 1. Executive Architecture Summary

**ProjectX - Student Project Exchange** is a production-grade multi-tier web application built following the classical **Model-View-Controller (MVC)** and **Data Access Object (DAO)** enterprise architectural patterns. It runs on an **Apache Tomcat 10.1** application container, powered by **Java 21 (OpenJDK)**, **Jakarta Servlets 6.0**, and communicates directly with a local **Oracle Database 21c Express Edition (XE)** instance over high-performance Oracle Thin JDBC (`ojdbc11`).

```mermaid
flowchart TD
    subgraph ClientLayer["Frontend Client Tier (Browser)"]
        UI["Modern Web Interface\n(HTML5, CSS3, Vanilla JS)"]
        API_CLIENT["REST Client (Fetch API)\nUserStore & Toast System"]
        UI <--> API_CLIENT
    end

    subgraph ContainerLayer["Web & Servlet Tier (Apache Tomcat 10.1)"]
        ROUTER["Tomcat Catalina Engine\nHTTP/1.1 Connector (Port 8080)"]
        
        subgraph Controllers["Jakarta Servlets 6.0 (Controllers)"]
            S_SERVLET["StudentServlet\n(/api/students/*)"]
            P_SERVLET["ProjectServlet\n(/api/projects/*)"]
            R_SERVLET["JoinRequestServlet\n(/api/requests/*)"]
            D_SERVLET["DashboardServlet\n(/api/dashboard)"]
            H_SERVLET["HealthCheckServlet\n(/api/health)"]
        end
        
        subgraph Utilities["Utilities & Middleware"]
            JSON_U["JsonUtil (Google Gson 2.10.1)"]
            CORS_U["CorsUtil (CORS Middleware)"]
            DB_U["DBUtil (Connection Factory)"]
        end

        ROUTER --> S_SERVLET
        ROUTER --> P_SERVLET
        ROUTER --> R_SERVLET
        ROUTER --> D_SERVLET
        ROUTER --> H_SERVLET

        Controllers -.-> JSON_U
        Controllers -.-> CORS_U
        Controllers --> DB_U
    end

    subgraph DAOLayer["Data Access Tier (Java 21)"]
        S_DAO["StudentDao"]
        P_DAO["ProjectDao"]
        R_DAO["JoinRequestDao"]
        A_DAO["AnalyticsDao"]
        
        S_SERVLET --> S_DAO
        P_SERVLET --> P_DAO
        R_SERVLET --> R_DAO
        D_SERVLET --> A_DAO
        A_DAO --> S_DAO
        A_DAO --> P_DAO
        A_DAO --> R_DAO
    end

    subgraph DatabaseLayer["Data Persistence Tier (Oracle Database 21c XE)"]
        LISTENER["Oracle TNS Listener\n(Port 1521 / TCP)"]
        XEPDB1["Pluggable Database: XEPDB1\nSchema: PROJECTX"]
        
        subgraph Tables["Relational Tables & Constraints"]
            T_STUDENTS["STUDENTS\n(Identity PK, Unique Email)"]
            T_PROJECTS["PROJECTS\n(Identity PK, FK Owner, Status Check)"]
            T_MEMBERS["PROJECT_MEMBERS\n(Identity PK, FKs, Unique Combo)"]
            T_REQUESTS["JOIN_REQUESTS\n(Identity PK, FKs, Unique Combo)"]
        end

        DB_U -- "Oracle Thin JDBC\n(TCP/IP Socket)" --> LISTENER
        LISTENER --> XEPDB1
        XEPDB1 --> Tables
    end
```

---

## 2. Layer-by-Layer Architectural Breakdown

### 2.1 Presentation Tier (Frontend)
- **HTML5 & Vanilla JavaScript**: Zero reliance on heavyweight frontend frameworks (React, Angular, Vue) ensures zero build-step overhead, rapid initial byte delivery, and full browser compatibility.
- **Custom CSS3 Design System**:
  - Implements cohesive CSS variables (`--primary: #4f46e5`, `--navy: #0f172a`, etc.) creating a modern student-tech aesthetic.
  - Fully responsive grid and flexbox layouts supporting mobile screens, tablets, and desktop workstations.
  - Interactive toast notification system (`Toast.show()`) and smooth animated modal dialogs (`Modal.open()`).
- **REST Communication (Fetch API)**:
  - Standardized JSON exchange using native `window.fetch()`.
  - Automatic base path resolution (`/projectx/api` vs `/api`).
  - Centralized request wrapper with HTTP error classification.
- **Demo Identity Selector (`UserStore`)**:
  - Persistent client-side demo state stored in browser `localStorage`.
  - Allows an evaluator to toggle active student personas (e.g. from *Aarav Sharma* to *Priya Patel*) in real time.
  - Automatically dispatches custom browser event `projectx:userChanged` to re-query role-dependent data dynamically without full-page reload.

### 2.2 Controller Tier (Jakarta Servlets 6.0)
- **Servlet Specification**: Uses `jakarta.servlet.*` (Jakarta EE 10 / Servlet API 6.0) targeting Apache Tomcat 10.1+.
- **RESTful Dispatching**:
  - `StudentServlet`: Handles collection (`/api/students`) and resource-by-ID (`/api/students/{id}`) for full CRUD operations.
  - `ProjectServlet`: Supports comprehensive multi-field filtering (`search`, `category`, `status`, `tech`, `ownerId`).
  - `JoinRequestServlet`: Coordinates join requests and transactional status state machines.
  - `DashboardServlet`: Gathers high-level aggregation metrics and personalized activity feeds.
  - `HealthCheckServlet`: Queries live Oracle Database metadata and reports driver and service health.
- **Serialization & Data Validation**:
  - Employs **Google Gson 2.10.1** with custom `Timestamp` type adapters for ISO-standard date formatting.
  - Centralized cross-origin handling via `CorsUtil` supporting standard CORS headers and `OPTIONS` preflight requests.

### 2.3 Data Access Tier (DAO Pattern)
- Decouples business logic and HTTP handlers from underlying SQL dialects.
- **Strict Parameterized Queries**: Every database interaction uses `java.sql.PreparedStatement` to eliminate SQL injection vulnerabilities.
- **Transactions & Atomicity**: Multi-step business operations (such as project creation automatically enrolling the creator as "Project Lead", or accepting a join request automatically inserting a row into `PROJECT_MEMBERS`) execute inside explicit database transactions with atomic `commit()` and `rollback()` handling.

### 2.4 Persistence Tier (Oracle Database 21c XE)
- **Pluggable Database Architecture**: Installed in dedicated PDB `XEPDB1` under the unprivileged schema user `PROJECTX`.
- **Identity Columns & Sequences**: Utilizes Oracle 21c `GENERATED BY DEFAULT AS IDENTITY` columns, with sequences aligned past initial seed data.
- **Referential Integrity**: Enforces foreign keys with `ON DELETE CASCADE`, check constraints for domain-restricted statuses, and unique constraints preventing duplicate emails or repeated join requests.

---

## 3. Request-Response Lifecycle Sequence

```mermaid
sequenceDiagram
    autonumber
    actor User as Student / Evaluator
    participant Browser as Web Browser (JS)
    participant Tomcat as Tomcat 10.1 Connector
    participant Servlet as ProjectServlet
    participant DAO as ProjectDao
    participant DBUtil as DBUtil
    participant Oracle as Oracle Database XE (1521)

    User->>Browser: Click "Publish Project"
    Browser->>Tomcat: POST /projectx/api/projects (JSON Payload)
    Tomcat->>Servlet: doPost(HttpServletRequest, HttpServletResponse)
    Servlet->>Servlet: Validate fields & parse JSON via Gson
    Servlet->>DAO: create(Project)
    DAO->>DBUtil: getConnection()
    DBUtil->>Oracle: DriverManager.getConnection(jdbc:oracle:thin:@...)
    Oracle-->>DBUtil: Established Connection
    DAO->>Oracle: PREPARE: INSERT INTO projects (...) VALUES (...)
    Oracle-->>DAO: Generated PROJECT_ID (e.g. 200)
    DAO->>Oracle: PREPARE: INSERT INTO project_members (Lead)
    DAO->>Oracle: COMMIT TRANSACTION
    DAO-->>Servlet: Returns populated Project entity
    Servlet->>Browser: HTTP 201 Created (JSON ApiResponse)
    Browser->>User: Displays success toast & redirects to Project Details
```
