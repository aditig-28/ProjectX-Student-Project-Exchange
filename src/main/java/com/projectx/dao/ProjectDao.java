package com.projectx.dao;

import com.projectx.model.Project;
import com.projectx.model.ProjectMember;
import com.projectx.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {

    public List<Project> getAll(String search, String category, String status, String technology) throws SQLException {
        List<Project> projects = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, s.full_name AS owner_name, s.email AS owner_email, s.department AS owner_dept, " +
            "  (SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.project_id) AS current_members " +
            "FROM projects p " +
            "JOIN students s ON p.owner_id = s.student_id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(p.title) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.required_skills) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append("AND LOWER(p.category) = LOWER(?) ");
            params.add(category.trim());
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("all")) {
            sql.append("AND LOWER(p.status) = LOWER(?) ");
            params.add(status.trim());
        }

        if (technology != null && !technology.trim().isEmpty()) {
            sql.append("AND LOWER(p.technologies) LIKE ? ");
            params.add("%" + technology.trim().toLowerCase() + "%");
        }

        sql.append("ORDER BY p.created_at DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs, false));
                }
            }
        }
        return projects;
    }

    public Project getById(Long id) throws SQLException {
        if (id == null) return null;
        String sql = "SELECT p.*, s.full_name AS owner_name, s.email AS owner_email, s.department AS owner_dept, " +
                     "  (SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.project_id) AS current_members " +
                     "FROM projects p " +
                     "JOIN students s ON p.owner_id = s.student_id " +
                     "WHERE p.project_id = ?";

        Project project = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    project = mapResultSetToProject(rs, true);
                }
            }
        }

        if (project != null) {
            project.setMembers(getMembers(id));
        }
        return project;
    }

    public List<Project> getByOwnerId(Long ownerId) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, s.full_name AS owner_name, s.email AS owner_email, s.department AS owner_dept, " +
                     "  (SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.project_id) AS current_members " +
                     "FROM projects p " +
                     "JOIN students s ON p.owner_id = s.student_id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY p.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs, false));
                }
            }
        }
        return projects;
    }

    public List<Project> getJoinedByStudent(Long studentId) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, s.full_name AS owner_name, s.email AS owner_email, s.department AS owner_dept, " +
                     "  (SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.project_id) AS current_members " +
                     "FROM projects p " +
                     "JOIN students s ON p.owner_id = s.student_id " +
                     "JOIN project_members mem ON p.project_id = mem.project_id " +
                     "WHERE mem.student_id = ? AND p.owner_id <> ? " +
                     "ORDER BY mem.joined_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs, false));
                }
            }
        }
        return projects;
    }

    public Project create(Project project) throws SQLException {
        String sql = "INSERT INTO projects (title, description, category, technologies, owner_id, team_size, " +
                     "required_skills, status, github_url, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        String[] returnCols = {"PROJECT_ID"};
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, returnCols)) {
                ps.setString(1, project.getTitle());
                ps.setString(2, project.getDescription());
                ps.setString(3, project.getCategory());
                ps.setString(4, project.getTechnologies());
                ps.setLong(5, project.getOwnerId());
                ps.setInt(6, project.getTeamSize() > 0 ? project.getTeamSize() : 4);
                ps.setString(7, project.getRequiredSkills());
                ps.setString(8, project.getStatus() != null ? project.getStatus() : "Open");
                ps.setString(9, project.getGithubUrl());

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            project.setProjectId(rs.getLong(1));
                        }
                    }
                }
            }

            // Automatically add project owner as Project Lead member
            if (project.getProjectId() != null) {
                String memberSql = "INSERT INTO project_members (project_id, student_id, role, joined_at) " +
                                   "VALUES (?, ?, 'Project Lead', CURRENT_TIMESTAMP)";
                try (PreparedStatement psMember = conn.prepareStatement(memberSql)) {
                    psMember.setLong(1, project.getProjectId());
                    psMember.setLong(2, project.getOwnerId());
                    psMember.executeUpdate();
                }
            }

            conn.commit();
            return project;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean update(Project project) throws SQLException {
        String sql = "UPDATE projects SET title = ?, description = ?, category = ?, technologies = ?, " +
                     "team_size = ?, required_skills = ?, status = ?, github_url = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE project_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getCategory());
            ps.setString(4, project.getTechnologies());
            ps.setInt(5, project.getTeamSize());
            ps.setString(6, project.getRequiredSkills());
            ps.setString(7, project.getStatus());
            ps.setString(8, project.getGithubUrl());
            ps.setLong(9, project.getProjectId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Long projectId) throws SQLException {
        String sql = "DELETE FROM projects WHERE project_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<ProjectMember> getMembers(Long projectId) throws SQLException {
        List<ProjectMember> members = new ArrayList<>();
        String sql = "SELECT pm.member_id, pm.project_id, pm.student_id, pm.role, pm.joined_at, " +
                     "s.full_name, s.email, s.department, s.skills, s.avatar_url " +
                     "FROM project_members pm " +
                     "JOIN students s ON pm.student_id = s.student_id " +
                     "WHERE pm.project_id = ? " +
                     "ORDER BY pm.joined_at ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectMember m = new ProjectMember();
                    m.setMemberId(rs.getLong("member_id"));
                    m.setProjectId(rs.getLong("project_id"));
                    m.setStudentId(rs.getLong("student_id"));
                    m.setRole(rs.getString("role"));
                    m.setJoinedAt(rs.getTimestamp("joined_at"));
                    m.setStudentName(rs.getString("full_name"));
                    m.setStudentEmail(rs.getString("email"));
                    m.setStudentDepartment(rs.getString("department"));
                    m.setStudentSkills(rs.getString("skills"));
                    m.setStudentAvatar(rs.getString("avatar_url"));
                    members.add(m);
                }
            }
        }
        return members;
    }

    public boolean addMember(Long projectId, Long studentId, String role) throws SQLException {
        String sql = "INSERT INTO project_members (project_id, student_id, role, joined_at) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, studentId);
            ps.setString(3, role != null ? role : "Contributor");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeMember(Long projectId, Long studentId) throws SQLException {
        String sql = "DELETE FROM project_members WHERE project_id = ? AND student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Project mapResultSetToProject(ResultSet rs, boolean detail) throws SQLException {
        Project p = new Project();
        p.setProjectId(rs.getLong("project_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setTechnologies(rs.getString("technologies"));
        p.setOwnerId(rs.getLong("owner_id"));
        p.setTeamSize(rs.getInt("team_size"));
        p.setRequiredSkills(rs.getString("required_skills"));
        p.setStatus(rs.getString("status"));
        p.setGithubUrl(rs.getString("github_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        p.setOwnerName(rs.getString("owner_name"));
        p.setOwnerEmail(rs.getString("owner_email"));
        p.setOwnerDepartment(rs.getString("owner_dept"));

        int membersCount = rs.getInt("current_members");
        p.setCurrentMembers(membersCount);
        p.setSpotsRemaining(Math.max(0, p.getTeamSize() - membersCount));

        return p;
    }
}
