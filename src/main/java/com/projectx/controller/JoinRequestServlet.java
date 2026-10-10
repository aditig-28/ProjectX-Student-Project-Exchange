package com.projectx.controller;

import com.projectx.dao.JoinRequestDao;
import com.projectx.dao.ProjectDao;
import com.projectx.dao.StudentDao;
import com.projectx.model.ApiResponse;
import com.projectx.model.JoinRequest;
import com.projectx.model.Project;
import com.projectx.model.Student;
import com.projectx.util.CorsUtil;
import com.projectx.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "JoinRequestServlet", urlPatterns = {"/api/requests", "/api/requests/*"})
public class JoinRequestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(JoinRequestServlet.class.getName());
    private final JoinRequestDao requestDao = new JoinRequestDao();
    private final ProjectDao projectDao = new ProjectDao();
    private final StudentDao studentDao = new StudentDao();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        CorsUtil.handleOptions(resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                String ownerIdParam = req.getParameter("ownerId");
                String studentIdParam = req.getParameter("studentId");
                String projectIdParam = req.getParameter("projectId");

                List<JoinRequest> list;
                if (ownerIdParam != null && !ownerIdParam.trim().isEmpty()) {
                    Long ownerId = Long.parseLong(ownerIdParam.trim());
                    list = requestDao.getIncomingForOwner(ownerId);
                } else if (studentIdParam != null && !studentIdParam.trim().isEmpty()) {
                    Long studentId = Long.parseLong(studentIdParam.trim());
                    list = requestDao.getOutgoingForStudent(studentId);
                } else if (projectIdParam != null && !projectIdParam.trim().isEmpty()) {
                    Long projectId = Long.parseLong(projectIdParam.trim());
                    list = requestDao.getByProject(projectId);
                } else {
                    // Default to all incoming for demo owner 1
                    list = requestDao.getIncomingForOwner(1L);
                }
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(list, "Fetched join requests successfully"));
            } else {
                Long id = parseId(pathInfo);
                if (id == null) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid request ID"));
                    return;
                }
                JoinRequest r = requestDao.getById(id);
                if (r == null) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Join request not found"));
                } else {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(r));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in JoinRequestServlet GET", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Database error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        try {
            JoinRequest request = JsonUtil.readJsonBody(req, JoinRequest.class);
            if (request == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid request body"));
                return;
            }

            if (request.getProjectId() == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Project ID is required"));
                return;
            }
            if (request.getStudentId() == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Student ID is required"));
                return;
            }

            // Verify project exists
            Project project = projectDao.getById(request.getProjectId());
            if (project == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Project not found"));
                return;
            }

            // Check if student exists
            Student applicant = studentDao.getById(request.getStudentId());
            if (applicant == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Student applicant not found"));
                return;
            }

            // Prevent owner from requesting to join their own project
            if (project.getOwnerId().equals(request.getStudentId())) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponse.error(400, "You cannot submit a join request to your own project"));
                return;
            }

            // Prevent duplicate join request
            if (requestDao.exists(request.getProjectId(), request.getStudentId())) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_CONFLICT,
                        ApiResponse.error(409, "A join request has already been submitted for this project"));
                return;
            }

            // Check if project is full or not open
            if ("Completed".equalsIgnoreCase(project.getStatus())) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponse.error(400, "Cannot join a project that is already marked as Completed"));
                return;
            }

            JoinRequest created = requestDao.create(request);
            if (created != null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(created, "Join request submitted successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Failed to submit join request"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in JoinRequestServlet POST", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        String pathInfo = req.getPathInfo();
        Long id = parseId(pathInfo);

        if (id == null) {
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Missing or invalid request ID"));
            return;
        }

        try {
            JoinRequest existing = requestDao.getById(id);
            if (existing == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Join request not found"));
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> body = JsonUtil.readJsonBody(req, Map.class);
            if (body == null || !body.containsKey("status")) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Missing 'status' in request body"));
                return;
            }

            String newStatus = body.get("status").toString().trim();
            if (!newStatus.equalsIgnoreCase("Accepted") && !newStatus.equalsIgnoreCase("Rejected")) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Status must be 'Accepted' or 'Rejected'"));
                return;
            }

            boolean updated = requestDao.updateStatus(id, newStatus);
            if (updated) {
                JoinRequest refreshed = requestDao.getById(id);
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK,
                        ApiResponse.ok(refreshed, "Join request status updated to " + newStatus));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Failed to update request status"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in JoinRequestServlet PUT", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Server error: " + e.getMessage()));
        }
    }

    private Long parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.trim().isEmpty() || pathInfo.equals("/")) {
            return null;
        }
        try {
            String clean = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
            if (clean.contains("/")) {
                clean = clean.substring(0, clean.indexOf("/"));
            }
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
