package com.projectx.dao;

import com.projectx.model.DashboardSummary;
import com.projectx.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsDao {

    private final StudentDao studentDao = new StudentDao();
    private final ProjectDao projectDao = new ProjectDao();
    private final JoinRequestDao requestDao = new JoinRequestDao();

    public DashboardSummary getPlatformStats() throws SQLException {
        DashboardSummary summary = new DashboardSummary();
        String sql = "SELECT " +
                     "  (SELECT COUNT(*) FROM students) AS total_students, " +
                     "  (SELECT COUNT(*) FROM projects) AS total_projects, " +
                     "  (SELECT COUNT(*) FROM projects WHERE status = 'Open') AS open_projects, " +
                     "  (SELECT COUNT(*) FROM project_members) AS active_collaborations, " +
                     "  (SELECT COUNT(*) FROM join_requests) AS total_requests " +
                     "FROM dual";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                summary.setTotalStudents(rs.getInt("total_students"));
                summary.setTotalProjects(rs.getInt("total_projects"));
                summary.setOpenProjects(rs.getInt("open_projects"));
                summary.setActiveCollaborations(rs.getInt("active_collaborations"));
                summary.setTotalRequests(rs.getInt("total_requests"));
            }
        }

        // Category breakdown
        summary.setCategoryDistribution(getCategoryDistribution());
        return summary;
    }

    public DashboardSummary getStudentDashboard(Long studentId) throws SQLException {
        DashboardSummary summary = getPlatformStats();
        if (studentId == null) return summary;

        summary.setCurrentStudentId(studentId);
        summary.setCurrentStudent(studentDao.getById(studentId));

        summary.setMyProjects(projectDao.getByOwnerId(studentId));
        summary.setMyProjectsCount(summary.getMyProjects().size());

        summary.setJoinedProjects(projectDao.getJoinedByStudent(studentId));
        summary.setMyMembershipsCount(summary.getJoinedProjects().size());

        summary.setIncomingRequests(requestDao.getIncomingForOwner(studentId));
        summary.setIncomingRequestsCount(summary.getIncomingRequests().size());

        summary.setOutgoingRequests(requestDao.getOutgoingForStudent(studentId));
        summary.setOutgoingRequestsCount(summary.getOutgoingRequests().size());

        return summary;
    }

    public Map<String, Integer> getCategoryDistribution() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT category, COUNT(*) as cnt FROM projects GROUP BY category ORDER BY cnt DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getInt("cnt"));
            }
        }
        return map;
    }
}
