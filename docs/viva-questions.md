# ProjectX - College Viva Voce & External Examiner Q&A Guide

This guide prepares you for common, deep-dive, and architectural questions typically asked by external examiners during full-stack college capstone evaluations.

---

## 1. Architecture & Java Servlet Fundamentals

### Q1: What architectural pattern does ProjectX follow, and why?
**Answer:**
ProjectX follows the **Model-View-Controller (MVC)** and **Data Access Object (DAO)** enterprise architectural patterns:
- **View (Frontend)**: Responsive HTML5, CSS3, and Vanilla JavaScript interacting with the backend via asynchronous `fetch()` calls.
- **Controller (Jakarta Servlets 6.0)**: Classes extending `HttpServlet` (`StudentServlet`, `ProjectServlet`, `JoinRequestServlet`, etc.) that intercept HTTP requests, validate incoming payloads, parse JSON, and invoke the appropriate business methods.
- **Model (Entity POJOs)**: Pure Java objects (`Student`, `Project`, `JoinRequest`, etc.) representing data structures.
- **DAO (Data Access Object)**: Encapsulates all SQL execution, connection handling, and result set mapping, completely decoupling the controller layer from the database schema.

### Q2: What is the lifecycle of a Java Servlet?
**Answer:**
A Servlet lifecycle consists of four main phases managed by the Tomcat Servlet Container:
1. **Loading & Instantiation**: Tomcat loads the Servlet class and creates an instance (usually on the first request or upon server startup if `load-on-startup` is specified).
2. **Initialization (`init()`)**: Executed exactly once after instantiation to initialize resources.
3. **Request Handling (`service()`)**: For each incoming HTTP request, Tomcat creates or assigns a worker thread that invokes `service()`, which delegates to `doGet()`, `doPost()`, `doPut()`, `doDelete()`, etc., passing `HttpServletRequest` and `HttpServletResponse`.
4. **Destruction (`destroy()`)**: Executed once when the web container shuts down or the application is undeployed, releasing locks and resources.

### Q3: Why did you choose Jakarta Servlet API 6.0 and Apache Tomcat 10.1 rather than `javax.servlet` / Tomcat 9?
**Answer:**
With the transition of Java EE to the Eclipse Foundation under the Jakarta EE initiative, the package namespace changed from `javax.servlet` to `jakarta.servlet` starting with Jakarta EE 9. Apache Tomcat 10.1 implements **Jakarta Servlet 6.0 (Jakarta EE 10)**, runs natively on **Java 11, 17, and 21**, and provides the modern, long-term supported foundation for contemporary enterprise Java development.

---

## 2. JDBC & Database Connectivity

### Q4: What is the difference between `Statement` and `PreparedStatement` in JDBC? Why is `PreparedStatement` mandatory in ProjectX?
**Answer:**
- **`Statement`**: Compiles the SQL query string on the database engine on every single execution. If user input is concatenated directly into the query, it introduces fatal **SQL Injection (SQLi)** vulnerabilities.
- **`PreparedStatement`**: Pre-compiles the SQL query template once on the database engine with parameter placeholders (`?`). Values are bound separately via typed setters (`ps.setString(1, val)`, `ps.setLong(2, id)`).
- **Advantages in ProjectX**:
  1. **Security**: Completely prevents SQL injection because parameters are treated strictly as literal data, never as executable SQL code.
  2. **Performance**: Enables the Oracle SQL engine to reuse execution plans across repeated calls (cursor caching).
  3. **Type Safety**: Automatically converts Java data types to matching Oracle SQL data types (`TIMESTAMP`, `VARCHAR2`, `NUMBER`).

### Q5: What type of JDBC Driver does ProjectX use?
**Answer:**
ProjectX uses the **Oracle Thin Driver (`ojdbc11.jar`)**, which is a **Type 4 JDBC Driver (Pure Java Driver)**:
- It connects directly from the Java runtime to the Oracle TNS Listener on port 1521 over TCP/IP using Oracle's proprietary Net8 (TNS) protocol.
- It requires no client-side native C/C++ libraries, Oracle Instant Client installation, or operating system configuration, making it fully portable across Windows, Linux, and macOS.

### Q6: How does ProjectX protect database credentials from being committed to source control?
**Answer:**
In accordance with the **Twelve-Factor App methodology**, configuration is segregated from code:
- Credentials are read from `db.properties` at runtime.
- The `DBUtil` class checks environment variables (`PROJECTX_DB_URL`, `PROJECTX_DB_USER`, `PROJECTX_DB_PASSWORD`) and JVM system properties (`-Ddb.url=...`) first, which take precedence over the properties file.
- The template `db.properties.example` is tracked in Git with dummy placeholders, while actual credentials remain isolated.

---

## 3. Database Design & Relational Concepts

### Q7: Explain the normalization level of your database schema.
**Answer:**
The schema is normalized to **Third Normal Form (3NF)**:
- **1NF**: All table columns store atomic values (no nested tables or comma-separated foreign keys), and each table has an explicit primary key.
- **2NF**: All non-key attributes are fully functionally dependent on the entire primary key, eliminating partial dependencies.
- **3NF**: There are no transitive dependencies (non-key columns do not depend on other non-key columns). For example, `PROJECTS` only stores `owner_id`. The owner's name, email, and department reside solely in `STUDENTS` and are retrieved using relational `JOIN` operations.

### Q8: What are Oracle Identity Columns and why were sequence restarts required?
**Answer:**
Oracle 12c+ introduced `GENERATED AS IDENTITY` columns to replace legacy sequence-trigger pairs:
- Under the hood, Oracle associates a system-generated sequence with the column.
- When sample data with hardcoded explicit IDs (e.g. `1..6`) was inserted, the internal sequence counter did not advance automatically.
- To prevent primary key uniqueness conflicts (`ORA-00001`) during runtime insertions, `ALTER TABLE ... MODIFY ... GENERATED AS IDENTITY (START WITH 100)` was executed, ensuring new records receive collision-free IDs starting above the seed data range.

### Q9: How is data integrity guaranteed when a project owner accepts a join request?
**Answer:**
The transaction is executed atomically using JDBC transaction demarcation:
```java
conn.setAutoCommit(false);
// 1. Update join request status to 'Accepted'
// 2. Insert new row into PROJECT_MEMBERS with role 'Collaborator'
conn.commit();
```
If an error occurs at any point, `conn.rollback()` is invoked, ensuring that a join request is never marked as accepted without the applicant being enrolled in the team roster.

---

## 4. RESTful API & Frontend Architecture

### Q10: How do HTTP status codes map to API outcomes in ProjectX?
**Answer:**
- **`200 OK`**: Successful query retrieval (`GET`) or successful resource update (`PUT`) / deletion (`DELETE`).
- **`201 Created`**: Successful creation of a new entity (`POST /api/students`, `POST /api/projects`, `POST /api/requests`).
- **`400 Bad Request`**: Malformed JSON or client validation failures (e.g., student applying to their own project).
- **`404 Not Found`**: Entity with the requested ID does not exist.
- **`409 Conflict`**: Uniqueness violations (duplicate student email address or duplicate join request for the same project).
- **`500 Internal Server Error`**: Unexpected server-side or database communication faults.

### Q11: Why is Vanilla JavaScript preferred over React or Angular for this submission?
**Answer:**
1. **Zero Build Tooling Overhead**: No Node.js bundling, Webpack/Vite transpilations, or `node_modules` weight. The frontend files are served directly as static resources by Apache Tomcat.
2. **Direct Standards-Based Interaction**: Uses native browser standards (`fetch()`, `CustomEvent`, CSS variables, DOM manipulation) proving mastery of fundamental web standards.
3. **Instant Portability**: Works out of the box on any standard browser without configuration.
