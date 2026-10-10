-- ============================================================================
-- ProjectX: Core SQL Queries & Analytics
-- Database: Oracle Database 21c / XE
-- ============================================================================

-- 1. Fetch all projects with Owner Details and Member Count
SELECT p.project_id,
       p.title,
       p.description,
       p.category,
       p.technologies,
       p.status,
       p.team_size,
       p.required_skills,
       p.github_url,
       p.created_at,
       s.student_id AS owner_id,
       s.full_name  AS owner_name,
       s.email      AS owner_email,
       s.department AS owner_dept,
       (SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.project_id) AS current_members,
       (p.team_size - (SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.project_id)) AS spots_remaining
FROM projects p
JOIN students s ON p.owner_id = s.student_id
ORDER BY p.created_at DESC;

-- 2. Search projects by title or description and filter by category and status
SELECT p.*, s.full_name AS owner_name
FROM projects p
JOIN students s ON p.owner_id = s.student_id
WHERE (LOWER(p.title) LIKE LOWER('%iot%') OR LOWER(p.description) LIKE LOWER('%iot%'))
  AND p.category = 'IoT & Embedded'
  AND p.status = 'Open'
ORDER BY p.created_at DESC;

-- 3. Get Student Profile with Project Statistics
SELECT s.*,
       (SELECT COUNT(*) FROM projects WHERE owner_id = s.student_id) AS projects_created,
       (SELECT COUNT(*) FROM project_members WHERE student_id = s.student_id) AS teams_joined,
       (SELECT COUNT(*) FROM join_requests WHERE student_id = s.student_id AND status = 'Pending') AS pending_requests_sent
FROM students s
WHERE s.student_id = 1;

-- 4. Get all Join Requests for projects owned by a specific student (Incoming requests)
SELECT jr.request_id,
       jr.project_id,
       p.title AS project_title,
       jr.student_id AS applicant_id,
       s.full_name   AS applicant_name,
       s.email       AS applicant_email,
       s.department  AS applicant_dept,
       s.skills      AS applicant_skills,
       jr.message,
       jr.status,
       jr.created_at,
       jr.responded_at
FROM join_requests jr
JOIN projects p ON jr.project_id = p.project_id
JOIN students s ON jr.student_id = s.student_id
WHERE p.owner_id = 1
ORDER BY jr.created_at DESC;

-- 5. Get all Join Requests submitted by a student (Outgoing requests)
SELECT jr.request_id,
       jr.project_id,
       p.title AS project_title,
       p.status AS project_status,
       owner_s.full_name AS owner_name,
       jr.message,
       jr.status,
       jr.created_at,
       jr.responded_at
FROM join_requests jr
JOIN projects p ON jr.project_id = p.project_id
JOIN students owner_s ON p.owner_id = owner_s.student_id
WHERE jr.student_id = 1
ORDER BY jr.created_at DESC;

-- 6. Overall Platform Statistics (Landing Page Metric Cards)
SELECT 
    (SELECT COUNT(*) FROM students) AS total_students,
    (SELECT COUNT(*) FROM projects) AS total_projects,
    (SELECT COUNT(*) FROM projects WHERE status = 'Open') AS open_projects,
    (SELECT COUNT(*) FROM project_members) AS active_collaborations,
    (SELECT COUNT(*) FROM join_requests) AS total_requests
FROM dual;

-- 7. Distribution of Projects by Category (Analytics)
SELECT category, COUNT(*) AS project_count
FROM projects
GROUP BY category
ORDER BY project_count DESC;
