-- ============================================================================
-- ProjectX: Sample Data for Demonstration
-- Compatible with Oracle Database 21c / XE
-- ============================================================================
SET DEFINE OFF;

-- Clean existing data
DELETE FROM project_members;
DELETE FROM join_requests;
DELETE FROM projects;
DELETE FROM students;
COMMIT;

-- ----------------------------------------------------------------------------
-- 1. Insert Sample Students
-- ----------------------------------------------------------------------------
INSERT INTO students (student_id, full_name, email, department, year_of_study, skills, bio, avatar_url)
VALUES (1, 'Aarav Sharma', 'aarav.sharma@college.edu', 'Computer Science and Engineering', '3rd Year',
        'Java, Spring Boot, Oracle, Microservices, Docker',
        'Full-stack enthusiast interested in scalable distributed systems and backend architecture. Looking for teammates to build production-grade web systems.',
        'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80');

INSERT INTO students (student_id, full_name, email, department, year_of_study, skills, bio, avatar_url)
VALUES (2, 'Priya Patel', 'priya.patel@college.edu', 'Information Technology', '4th Year',
        'Python, TensorFlow, PyTorch, Computer Vision, FastAPI',
        'Final-year IT undergrad researching deep learning models and edge AI deployment. Keen to collaborate on medical imaging or NLP capstones.',
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80');

INSERT INTO students (student_id, full_name, email, department, year_of_study, skills, bio, avatar_url)
VALUES (3, 'Rohan Mehta', 'rohan.mehta@college.edu', 'Electronics & Telecommunication', '3rd Year',
        'C++, Embedded C, IoT, MQTT, Arduino, FreeRTOS',
        'Hardware tinkerer bridging physical sensors with cloud telemetry dashboards. Excited about smart campus automation and drone avionics.',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80');

INSERT INTO students (student_id, full_name, email, department, year_of_study, skills, bio, avatar_url)
VALUES (4, 'Ananya Deshmukh', 'ananya.deshmukh@college.edu', 'Computer Science and Engineering', '2nd Year',
        'HTML5, CSS3, JavaScript, React, UI/UX Design, Figma',
        'Frontend craftsman with an obsession for clean accessibility, micro-animations, and fluid responsive design systems.',
        'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=200&q=80');

INSERT INTO students (student_id, full_name, email, department, year_of_study, skills, bio, avatar_url)
VALUES (5, 'Kavya Nair', 'kavya.nair@college.edu', 'Data Science & AI', '3rd Year',
        'Python, SQL, Power BI, Pandas, Scikit-learn, Statistics',
        'Passionate about mining actionable insights from messy real-world datasets. Building predictive models for student engagement and academic analytics.',
        'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=200&q=80');

INSERT INTO students (student_id, full_name, email, department, year_of_study, skills, bio, avatar_url)
VALUES (6, 'Vikram Joshi', 'vikram.joshi@college.edu', 'Cybersecurity', '4th Year',
        'Network Security, Kali Linux, Penetration Testing, OWASP Top 10, Go',
        'Ethical hacker and security researcher focused on zero-trust application architectures and secure code audits.',
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80');

-- ----------------------------------------------------------------------------
-- 2. Insert Sample Projects
-- ----------------------------------------------------------------------------
INSERT INTO projects (project_id, title, description, category, technologies, owner_id, team_size, required_skills, status, github_url)
VALUES (101, 'EcoTrack: Campus Energy & Carbon Footprint Monitor',
        'An IoT-powered telemetry platform collecting live power consumption metrics across collegiate laboratory buildings. Includes anomaly detection and energy conservation recommendations.',
        'IoT & Embedded', 'Embedded C, MQTT, Java, Oracle, Chart.js', 3, 4,
        'Embedded Systems, MQTT, Backend Java, UI Dashboards', 'Open', 'https://github.com/campus-ecotrack');

INSERT INTO projects (project_id, title, description, category, technologies, owner_id, team_size, required_skills, status, github_url)
VALUES (102, 'NeuroScan: Automated Retinal Pathology Detection',
        'A clinical decision support prototype leveraging convolutional neural networks to screen digital fundus images for diabetic retinopathy lesions with graded risk confidence intervals.',
        'Artificial Intelligence', 'Python, PyTorch, OpenCV, Flask, Docker', 2, 3,
        'Deep Learning, Computer Vision, Python Backend', 'In Progress', 'https://github.com/neuroscan-ai');

INSERT INTO projects (project_id, title, description, category, technologies, owner_id, team_size, required_skills, status, github_url)
VALUES (103, 'CodeCollab: Real-Time Peer Pair Programming IDE',
        'A lightweight in-browser collaborative workspace featuring operational-transformation code synchronization, integrated compiler sandboxing, and WebRTC peer audio rooms.',
        'Web Development', 'Java Servlets, WebSockets, JavaScript, Monaco Editor, Oracle', 1, 5,
        'Java, WebSockets, Vanilla JS, Frontend UI', 'Open', 'https://github.com/codecollab-ide');

INSERT INTO projects (project_id, title, description, category, technologies, owner_id, team_size, required_skills, status, github_url)
VALUES (104, 'ShieldVault: Decentralized Credential Attestation',
        'Cryptographically verifiable credential issuer enabling colleges to issue tamper-evident diplomas and micro-certifications with zero-knowledge verification.',
        'Cybersecurity', 'Java, Cryptography, SHA-256, Oracle, REST API', 6, 4,
        'Applied Cryptography, Java Backend, Security Auditing', 'Open', 'https://github.com/shield-vault');

INSERT INTO projects (project_id, title, description, category, technologies, owner_id, team_size, required_skills, status, github_url)
VALUES (105, 'CampusPulse: Student Wellbeing & Sentiment Analytics',
        'An anonymous feedback and mental wellness aggregator synthesizing course evaluation trends, workload surges, and sentiment indices for departmental heads.',
        'Data Science', 'Python, NLTK, Oracle Database, D3.js, REST', 5, 3,
        'NLP, Data Mining, Interactive Data Visualization', 'Completed', 'https://github.com/campuspulse-ds');

INSERT INTO projects (project_id, title, description, category, technologies, owner_id, team_size, required_skills, status, github_url)
VALUES (106, 'SmartShelf: Automated RFID Library Inventory System',
        'An embedded shelf-reader setup that tracks misplaced books in real-time, alerts librarians of inventory discrepancy, and facilitates instant mobile checkouts.',
        'IoT & Embedded', 'C++, Arduino, Java Servlets, Oracle, Bootstrap', 3, 4,
        'RFID hardware interfacing, Java Web, Mobile web', 'In Progress', 'https://github.com/smartshelf-lib');

-- ----------------------------------------------------------------------------
-- 3. Insert Project Members (Accepted collaborators)
-- ----------------------------------------------------------------------------
INSERT INTO project_members (member_id, project_id, student_id, role)
VALUES (1, 102, 2, 'Project Lead');

INSERT INTO project_members (member_id, project_id, student_id, role)
VALUES (2, 102, 1, 'Backend Integration');

INSERT INTO project_members (member_id, project_id, student_id, role)
VALUES (3, 103, 1, 'Project Lead');

INSERT INTO project_members (member_id, project_id, student_id, role)
VALUES (4, 103, 4, 'Frontend UI Architect');

INSERT INTO project_members (member_id, project_id, student_id, role)
VALUES (5, 105, 5, 'Project Lead');

-- ----------------------------------------------------------------------------
-- 4. Insert Join Requests (Pending, Accepted, Rejected)
-- ----------------------------------------------------------------------------
INSERT INTO join_requests (request_id, project_id, student_id, message, status, responded_at)
VALUES (1, 102, 1, 'Hi Priya, I have experience setting up Java REST APIs to wrap machine learning inferences. Would love to help!', 'Accepted', CURRENT_TIMESTAMP);

INSERT INTO join_requests (request_id, project_id, student_id, message, status, responded_at)
VALUES (2, 103, 4, 'Hey Aarav! I would love to build the Monaco editor integration and UI components for CodeCollab.', 'Accepted', CURRENT_TIMESTAMP);

INSERT INTO join_requests (request_id, project_id, student_id, message, status, responded_at)
VALUES (3, 101, 1, 'Hi Rohan, I can build the backend ingestion pipeline for your campus energy telemetry sensors using Java and Oracle.', 'Pending', NULL);

INSERT INTO join_requests (request_id, project_id, student_id, message, status, responded_at)
VALUES (4, 103, 6, 'Interested in implementing secure sandbox execution to prevent malicious code execution.', 'Pending', NULL);

INSERT INTO join_requests (request_id, project_id, student_id, message, status, responded_at)
VALUES (5, 104, 4, 'I can create a clean dashboard for viewing certificates, though my crypto background is basic.', 'Pending', NULL);

INSERT INTO join_requests (request_id, project_id, student_id, message, status, responded_at)
VALUES (6, 101, 5, 'Can I contribute data analytics models for energy anomaly forecasting?', 'Pending', NULL);

COMMIT;

-- ----------------------------------------------------------------------------
-- 5. Advance Identity Sequences Past Seed Data
-- ----------------------------------------------------------------------------
ALTER TABLE students MODIFY student_id GENERATED BY DEFAULT AS IDENTITY (START WITH 100);
ALTER TABLE projects MODIFY project_id GENERATED BY DEFAULT AS IDENTITY (START WITH 200);
ALTER TABLE project_members MODIFY member_id GENERATED BY DEFAULT AS IDENTITY (START WITH 100);
ALTER TABLE join_requests MODIFY request_id GENERATED BY DEFAULT AS IDENTITY (START WITH 100);
