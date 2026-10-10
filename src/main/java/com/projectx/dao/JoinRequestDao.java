package com.projectx.dao;

import com.projectx.model.JoinRequest;
import com.projectx.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JoinRequestDao {

    public List<JoinRequest> getIncomingForOwner(Long ownerId) throws SQLException {
        List<JoinRequest> list = new ArrayList<>();
        String sql = "SELECT jr.*, p.title AS project_title, p.category AS project_category, p.owner_id AS project_owner_id, " +
                     "       s.full_name AS applicant_name, s.email AS applicant_email, s.department AS applicant_dept, " +
                     "       s.skills AS applicant_skills, s.avatar_url AS applicant_avatar " +
                     "FROM join_requests jr " +
                     "JOIN projects p ON jr.project_id = p.project_id " +
                     "JOIN students s ON jr.student_id = s.student_id " +
                     "WHERE p.owner_id = ? " +
                     "ORDER BY jr.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRequest(rs));
                }
            }
        }
        return list;
    }

    public List<JoinRequest> getOutgoingForStudent(Long studentId) throws SQLException {
        List<JoinRequest> list = new ArrayList<>();
        String sql = "SELECT jr.*, p.title AS project_title, p.category AS project_category, p.owner_id AS project_owner_id, " +
                     "       s.full_name AS applicant_name, s.email AS applicant_email, s.department AS applicant_dept, " +
                     "       s.skills AS applicant_skills, s.avatar_url AS applicant_avatar " +
                     "FROM join_requests jr " +
                     "JOIN projects p ON jr.project_id = p.project_id " +
                     "JOIN students s ON jr.student_id = s.student_id " +
                     "WHERE jr.student_id = ? " +
                     "ORDER BY jr.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRequest(rs));
                }
            }
        }
        return list;
    }

    public List<JoinRequest> getByProject(Long projectId) throws SQLException {
        List<JoinRequest> list = new ArrayList<>();
        String sql = "SELECT jr.*, p.title AS project_title, p.category AS project_category, p.owner_id AS project_owner_id, " +
                     "       s.full_name AS applicant_name, s.email AS applicant_email, s.department AS applicant_dept, " +
                     "       s.skills AS applicant_skills, s.avatar_url AS applicant_avatar " +
                     "FROM join_requests jr " +
                     "JOIN projects p ON jr.project_id = p.project_id " +
                     "JOIN students s ON jr.student_id = s.student_id " +
                     "WHERE jr.project_id = ? " +
                     "ORDER BY jr.created_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRequest(rs));
                }
            }
        }
        return list;
    }

    public JoinRequest getById(Long id) throws SQLException {
        if (id == null) return null;
        String sql = "SELECT jr.*, p.title AS project_title, p.category AS project_category, p.owner_id AS project_owner_id, " +
                     "       s.full_name AS applicant_name, s.email AS applicant_email, s.department AS applicant_dept, " +
                     "       s.skills AS applicant_skills, s.avatar_url AS applicant_avatar " +
                     "FROM join_requests jr " +
                     "JOIN projects p ON jr.project_id = p.project_id " +
                     "JOIN students s ON jr.student_id = s.student_id " +
                     "WHERE jr.request_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        }
        return null;
    }

    public boolean exists(Long projectId, Long studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM join_requests WHERE project_id = ? AND student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public JoinRequest create(JoinRequest req) throws SQLException {
        String sql = "INSERT INTO join_requests (project_id, student_id, message, status, created_at) " +
                     "VALUES (?, ?, ?, 'Pending', CURRENT_TIMESTAMP)";

        String[] returnCols = {"REQUEST_ID"};
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, returnCols)) {
            ps.setLong(1, req.getProjectId());
            ps.setLong(2, req.getStudentId());
            ps.setString(3, req.getMessage());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        req.setRequestId(rs.getLong(1));
                    }
                }
                return getById(req.getRequestId());
            }
        }
        return null;
    }

    public boolean updateStatus(Long requestId, String newStatus) throws SQLException {
        JoinRequest req = getById(requestId);
        if (req == null) return false;

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE join_requests SET status = ?, responded_at = CURRENT_TIMESTAMP WHERE request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setLong(2, requestId);
                ps.executeUpdate();
            }

            // If Accepted, automatically add the applicant to project_members if not already a member
            if ("Accepted".equalsIgnoreCase(newStatus)) {
                String checkSql = "SELECT COUNT(*) FROM project_members WHERE project_id = ? AND student_id = ?";
                boolean alreadyMember = false;
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setLong(1, req.getProjectId());
                    checkPs.setLong(2, req.getStudentId());
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            alreadyMember = true;
                        }
                    }
                }

                if (!alreadyMember) {
                    String addMemberSql = "INSERT INTO project_members (project_id, student_id, role, joined_at) " +
                                          "VALUES (?, ?, 'Collaborator', CURRENT_TIMESTAMP)";
                    try (PreparedStatement addPs = conn.prepareStatement(addMemberSql)) {
                        addPs.setLong(1, req.getProjectId());
                        addPs.setLong(2, req.getStudentId());
                        addPs.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;
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

    private JoinRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        JoinRequest r = new JoinRequest();
        r.setRequestId(rs.getLong("request_id"));
        r.setProjectId(rs.getLong("project_id"));
        r.setStudentId(rs.getLong("student_id"));
        r.setMessage(rs.getString("message"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setRespondedAt(rs.getTimestamp("responded_at"));

        r.setProjectTitle(rs.getString("project_title"));
        r.setProjectCategory(rs.getString("project_category"));
        r.setProjectOwnerId(rs.getLong("project_owner_id"));

        r.setApplicantName(rs.getString("applicant_name"));
        r.setApplicantEmail(rs.getString("applicant_email"));
        r.setApplicantDepartment(rs.getString("applicant_dept"));
        r.setApplicantSkills(rs.getString("applicant_skills"));
        r.setApplicantAvatar(rs.getString("applicant_avatar"));

        return r;
    }
}
