package com.projectx.dao;

import com.projectx.model.Student;
import com.projectx.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    public List<Student> getAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, " +
                     "  (SELECT COUNT(*) FROM projects p WHERE p.owner_id = s.student_id) AS projects_created, " +
                     "  (SELECT COUNT(*) FROM project_members pm WHERE pm.student_id = s.student_id) AS teams_joined, " +
                     "  (SELECT COUNT(*) FROM join_requests jr WHERE jr.student_id = s.student_id AND jr.status = 'Pending') AS pending_requests_sent " +
                     "FROM students s ORDER BY s.full_name ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs, true));
            }
        }
        return students;
    }

    public Student getById(Long id) throws SQLException {
        if (id == null) return null;
        String sql = "SELECT s.*, " +
                     "  (SELECT COUNT(*) FROM projects p WHERE p.owner_id = s.student_id) AS projects_created, " +
                     "  (SELECT COUNT(*) FROM project_members pm WHERE pm.student_id = s.student_id) AS teams_joined, " +
                     "  (SELECT COUNT(*) FROM join_requests jr WHERE jr.student_id = s.student_id AND jr.status = 'Pending') AS pending_requests_sent " +
                     "FROM students s WHERE s.student_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs, true);
                }
            }
        }
        return null;
    }

    public Student getByEmail(String email) throws SQLException {
        if (email == null) return null;
        String sql = "SELECT * FROM students WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs, false);
                }
            }
        }
        return null;
    }

    public boolean emailExists(String email, Long excludeStudentId) throws SQLException {
        if (email == null) return false;
        String sql = excludeStudentId == null ?
                "SELECT COUNT(*) FROM students WHERE LOWER(email) = LOWER(?)" :
                "SELECT COUNT(*) FROM students WHERE LOWER(email) = LOWER(?) AND student_id <> ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            if (excludeStudentId != null) {
                ps.setLong(2, excludeStudentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Student create(Student student) throws SQLException {
        String sql = "INSERT INTO students (full_name, email, department, year_of_study, skills, bio, avatar_url, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        String[] returnCols = {"STUDENT_ID"};
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, returnCols)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getEmail().trim().toLowerCase());
            ps.setString(3, student.getDepartment());
            ps.setString(4, student.getYearOfStudy());
            ps.setString(5, student.getSkills());
            ps.setString(6, student.getBio());
            ps.setString(7, student.getAvatarUrl());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setStudentId(rs.getLong(1));
                    }
                }
                return student;
            }
        }
        return null;
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET full_name = ?, email = ?, department = ?, year_of_study = ?, " +
                     "skills = ?, bio = ?, avatar_url = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE student_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getEmail().trim().toLowerCase());
            ps.setString(3, student.getDepartment());
            ps.setString(4, student.getYearOfStudy());
            ps.setString(5, student.getSkills());
            ps.setString(6, student.getBio());
            ps.setString(7, student.getAvatarUrl());
            ps.setLong(8, student.getStudentId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Long studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Student mapResultSetToStudent(ResultSet rs, boolean includeStats) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getLong("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setDepartment(rs.getString("department"));
        s.setYearOfStudy(rs.getString("year_of_study"));
        s.setSkills(rs.getString("skills"));
        s.setBio(rs.getString("bio"));
        s.setAvatarUrl(rs.getString("avatar_url"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));

        if (includeStats) {
            try {
                s.setProjectsCreated(rs.getInt("projects_created"));
                s.setTeamsJoined(rs.getInt("teams_joined"));
                s.setPendingRequestsSent(rs.getInt("pending_requests_sent"));
            } catch (SQLException ignored) {
            }
        }
        return s;
    }
}
