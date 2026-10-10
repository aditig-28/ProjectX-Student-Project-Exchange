package com.projectx.controller;

import com.projectx.dao.AnalyticsDao;
import com.projectx.model.ApiResponse;
import com.projectx.model.DashboardSummary;
import com.projectx.util.CorsUtil;
import com.projectx.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/api/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    private final AnalyticsDao analyticsDao = new AnalyticsDao();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        CorsUtil.handleOptions(resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        try {
            String studentIdParam = req.getParameter("studentId");
            DashboardSummary summary;
            if (studentIdParam != null && !studentIdParam.trim().isEmpty()) {
                Long studentId = Long.parseLong(studentIdParam.trim());
                summary = analyticsDao.getStudentDashboard(studentId);
            } else {
                summary = analyticsDao.getPlatformStats();
            }
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(summary, "Dashboard statistics loaded successfully"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in DashboardServlet", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Error loading dashboard metrics: " + e.getMessage()));
        }
    }
}
