package com.projectx.controller;

import com.projectx.dao.ProjectDao;
import com.projectx.dao.StudentDao;
import com.projectx.model.ApiResponse;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ProjectServlet", urlPatterns = {"/api/projects", "/api/projects/*"})
public class ProjectServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProjectServlet.class.getName());
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
                // Filter parameters
                String search = req.getParameter("search");
                String category = req.getParameter("category");
                String status = req.getParameter("status");
                String tech = req.getParameter("tech");
                String ownerIdParam = req.getParameter("ownerId");

                List<Project> list;
                if (ownerIdParam != null && !ownerIdParam.trim().isEmpty()) {
                    Long ownerId = Long.parseLong(ownerIdParam.trim());
                    list = projectDao.getByOwnerId(ownerId);
                } else {
                    list = projectDao.getAll(search, category, status, tech);
                }
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(list, "Fetched projects successfully"));
            } else {
                // GET /api/projects/{id}
                Long id = parseId(pathInfo);
                if (id == null) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid project ID"));
                    return;
                }
                Project project = projectDao.getById(id);
                if (project == null) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Project not found with ID " + id));
                } else {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(project));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ProjectServlet GET", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Database error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        try {
            Project project = JsonUtil.readJsonBody(req, Project.class);
            if (project == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid JSON payload"));
                return;
            }

            // Input Validations
            if (project.getTitle() == null || project.getTitle().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Project title is required"));
                return;
            }
            if (project.getDescription() == null || project.getDescription().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Project description is required"));
                return;
            }
            if (project.getCategory() == null || project.getCategory().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Category is required"));
                return;
            }
            if (project.getTechnologies() == null || project.getTechnologies().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Technologies are required"));
                return;
            }
            if (project.getRequiredSkills() == null || project.getRequiredSkills().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Required skills are required"));
                return;
            }
            if (project.getOwnerId() == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Owner/Student ID is required"));
                return;
            }

            // Verify owner student exists
            Student owner = studentDao.getById(project.getOwnerId());
            if (owner == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponse.error(400, "Student with owner ID " + project.getOwnerId() + " does not exist"));
                return;
            }

            if (project.getTeamSize() <= 0) {
                project.setTeamSize(4);
            }
            if (project.getStatus() == null || project.getStatus().trim().isEmpty()) {
                project.setStatus("Open");
            }

            Project created = projectDao.create(project);
            if (created != null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(created, "Project published successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Failed to create project"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ProjectServlet POST", e);
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
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Missing or invalid project ID"));
            return;
        }

        try {
            Project existing = projectDao.getById(id);
            if (existing == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Project not found"));
                return;
            }

            Project updateData = JsonUtil.readJsonBody(req, Project.class);
            if (updateData == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid JSON body"));
                return;
            }

            if (updateData.getTitle() != null && !updateData.getTitle().trim().isEmpty()) {
                existing.setTitle(updateData.getTitle().trim());
            }
            if (updateData.getDescription() != null && !updateData.getDescription().trim().isEmpty()) {
                existing.setDescription(updateData.getDescription().trim());
            }
            if (updateData.getCategory() != null && !updateData.getCategory().trim().isEmpty()) {
                existing.setCategory(updateData.getCategory().trim());
            }
            if (updateData.getTechnologies() != null && !updateData.getTechnologies().trim().isEmpty()) {
                existing.setTechnologies(updateData.getTechnologies().trim());
            }
            if (updateData.getRequiredSkills() != null && !updateData.getRequiredSkills().trim().isEmpty()) {
                existing.setRequiredSkills(updateData.getRequiredSkills().trim());
            }
            if (updateData.getTeamSize() > 0) {
                existing.setTeamSize(updateData.getTeamSize());
            }
            if (updateData.getStatus() != null && !updateData.getStatus().trim().isEmpty()) {
                existing.setStatus(updateData.getStatus().trim());
            }
            if (updateData.getGithubUrl() != null) {
                existing.setGithubUrl(updateData.getGithubUrl().trim());
            }

            boolean ok = projectDao.update(existing);
            if (ok) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(existing, "Project updated successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Could not update project"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ProjectServlet PUT", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        String pathInfo = req.getPathInfo();
        Long id = parseId(pathInfo);

        if (id == null) {
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Missing or invalid project ID"));
            return;
        }

        try {
            Project existing = projectDao.getById(id);
            if (existing == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Project not found"));
                return;
            }

            boolean deleted = projectDao.delete(id);
            if (deleted) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(null, "Project deleted successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Could not delete project"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ProjectServlet DELETE", e);
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
