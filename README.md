# ProjectX — Student Project Exchange Portal

A web-based platform that helps college students discover academic projects, publish project ideas, explore students with different technical skills, and collaborate by sending and managing team join requests.

## Overview

ProjectX aims to make student project collaboration easier by providing a central platform where students can share ideas, find collaborators, and manage project participation.

The application uses a web frontend, a Java Servlet backend, JDBC for database connectivity, and Oracle Database for storing application data.

## Objectives

- Provide a platform to publish and explore student projects.
- Allow students to search and filter projects by category and status.
- Support student registration and profile management.
- Enable students to request to join projects.
- Allow project owners to manage incoming join requests.
- Store and manage project and student information using a relational database.

## Technology Stack

| Component | Technology |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Backend | Java 21, Jakarta Servlets |
| Application Server | Apache Tomcat 10.1 |
| Database | Oracle Database XE 21c |
| Database Connectivity | JDBC |
| JSON Processing | Google Gson |
| Build Tool | Apache Maven |
| Version Control | Git and GitHub |

*The versions listed above should match the versions used in the actual project.*

## System Architecture

The application follows a layered architecture:

1. **Frontend:** HTML, CSS, and JavaScript provide the user interface.
2. **Servlet Layer:** Java Servlets receive HTTP requests and process application operations.
3. **DAO Layer:** Data Access Objects handle database queries.
4. **JDBC Layer:** JDBC connects the Java application to Oracle Database.
5. **Database Layer:** Oracle Database stores student details, projects, team membership, and join requests.

### Architecture Flow

```text
User / Web Browser
        |
        v
HTML, CSS and JavaScript
        |
        v
HTTP Requests and JSON
        |
        v
Java Servlets
        |
        v
Data Access Objects (DAO)
        |
        v
JDBC
        |
        v
Oracle Database
```

## Key Features

### 1. Project Management
- Create and publish project listings.
- View project descriptions and details.
- Update or delete project records, where supported.
- Search and filter projects by category or status.

### 2. Student Management
- Register student information.
- Browse the student directory.
- View and update student profiles, where supported.

### 3. Team Collaboration
- Send requests to join projects.
- View incoming and outgoing join requests.
- Accept or reject requests, where supported.
- Maintain project team membership.

### 4. Database Management
- Store student and project information in Oracle Database.
- Use JDBC to execute SQL queries.
- Apply database constraints to maintain data integrity.

## Database Design

The project is designed around related relational tables:

- **STUDENTS:** Stores student information.
- **PROJECTS:** Stores project details and ownership information.
- **PROJECT_MEMBERS:** Stores the relationship between projects and their team members.
- **JOIN_REQUESTS:** Stores requests to join projects and their statuses.

Primary keys and foreign keys help maintain relationships between records. The exact constraints depend on the database schema implemented in the project.

## CRUD Operations

ProjectX uses CRUD operations to manage application data:

| Operation | Description | Example |
|---|---|---|
| Create | Add a new record | Publish a project |
| Read | Retrieve existing records | View the project catalogue |
| Update | Modify an existing record | Edit project details |
| Delete | Remove a record | Delete a project, subject to database constraints |

These operations are handled by the backend and database layer rather than directly modifying database records from the browser.

## Getting Started

### Prerequisites

Install and configure the following tools:

- Java Development Kit (JDK)
- Apache Maven
- Apache Tomcat
- Oracle Database XE
- Git (optional, for version control)

### Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/aditig-28/ProjectX-Student-Project-Exchange.git
   ```

2. Open the project directory:

   ```bash
   cd ProjectX-Student-Project-Exchange
   ```

3. Configure the Oracle database connection using the project's configuration template. Set your local database URL, username, and password privately.

4. Execute the database schema script and, if required, the sample data script using Oracle SQL*Plus.

5. Build the application using Maven:

   ```bash
   mvn clean package
   ```

6. Deploy the generated WAR file to a compatible Apache Tomcat server.

7. Start Tomcat and open the application using the deployment context configured for your project.

**Note:** File paths, database scripts, and deployment settings may differ depending on the actual project configuration. Refer to the included setup scripts and configuration files.

## Testing

The application should be tested for:

- Creating, viewing, updating, and deleting records.
- Searching and filtering projects.
- Submitting and processing join requests.
- Database connectivity and data persistence.
- Input validation and handling invalid requests.
- Correct handling of database constraints and errors.

Record test results only after executing the corresponding tests.

## Future Enhancements

- Secure student authentication and role-based access control.
- Email notifications for project join requests.
- Advanced project recommendations based on student skills.
- Improved accessibility and mobile responsiveness.
- Deployment to a cloud hosting platform.

## Learning Outcomes

This project provides practical experience with:

- Full-stack web application development.
- Java Servlets and HTTP request handling.
- JDBC and relational database operations.
- SQL, database relationships, and data integrity.
- CRUD implementation and API integration.
- Software documentation and version control using Git and GitHub.

## Author

**Aditi Gawale**  
B.Tech — Computer Science and Engineering  
VIT Chennai

## References

- [Java Documentation](https://docs.oracle.com/en/java/)
- [Jakarta Servlet Documentation](https://jakarta.ee/specifications/servlet/)
- [Apache Tomcat Documentation](https://tomcat.apache.org/)
- [Oracle Database Documentation](https://docs.oracle.com/en/database/)
- [Apache Maven Documentation](https://maven.apache.org/guides/)

