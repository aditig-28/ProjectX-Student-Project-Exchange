package com.projectx.controller;

import com.projectx.dao.StudentDao;
import com.projectx.model.ApiResponse;
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

@WebServlet(name = "StudentServlet", urlPatterns = {"/api/students", "/api/students/*"})
public class StudentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(StudentServlet.class.getName());
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
                // GET /api/students
                List<Student> list = studentDao.getAll();
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(list, "Fetched students successfully"));
            } else {
                // GET /api/students/{id}
                Long id = parseId(pathInfo);
                if (id == null) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid student ID"));
                    return;
                }
                Student student = studentDao.getById(id);
                if (student == null) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Student with ID " + id + " not found"));
                } else {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(student));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in StudentServlet GET", e);
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.error(500, "Database error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        try {
            Student student = JsonUtil.readJsonBody(req, Student.class);
            if (student == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Invalid JSON payload"));
                return;
            }

            // Validation
            if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Full name is required"));
                return;
            }
            if (student.getEmail() == null || !student.getEmail().contains("@")) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "A valid email address is required"));
                return;
            }
            if (student.getDepartment() == null || student.getDepartment().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Department is required"));
                return;
            }
            if (student.getYearOfStudy() == null || student.getYearOfStudy().trim().isEmpty()) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Year of study is required"));
                return;
            }

            // Duplicate Email Check
            if (studentDao.emailExists(student.getEmail(), null)) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_CONFLICT,
                        ApiResponse.error(409, "Email address already registered: " + student.getEmail()));
                return;
            }

            Student created = studentDao.create(student);
            if (created != null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(created, "Student registered successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Failed to register student"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in StudentServlet POST", e);
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
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Missing or invalid student ID in path"));
            return;
        }

        try {
            Student existing = studentDao.getById(id);
            if (existing == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Student not found with ID " + id));
                return;
            }

            Student updateData = JsonUtil.readJsonBody(req, Student.class);
            if (updateData == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Empty or invalid JSON body"));
                return;
            }

            // Validation
            if (updateData.getFullName() != null && !updateData.getFullName().trim().isEmpty()) {
                existing.setFullName(updateData.getFullName().trim());
            }
            if (updateData.getEmail() != null && updateData.getEmail().contains("@")) {
                if (studentDao.emailExists(updateData.getEmail(), id)) {
                    JsonUtil.sendResponse(resp, HttpServletResponse.SC_CONFLICT,
                            ApiResponse.error(409, "Email address is already used by another student"));
                    return;
                }
                existing.setEmail(updateData.getEmail().trim());
            }
            if (updateData.getDepartment() != null && !updateData.getDepartment().trim().isEmpty()) {
                existing.setDepartment(updateData.getDepartment().trim());
            }
            if (updateData.getYearOfStudy() != null && !updateData.getYearOfStudy().trim().isEmpty()) {
                existing.setYearOfStudy(updateData.getYearOfStudy().trim());
            }
            if (updateData.getSkills() != null) {
                existing.setSkills(updateData.getSkills().trim());
            }
            if (updateData.getBio() != null) {
                existing.setBio(updateData.getBio().trim());
            }
            if (updateData.getAvatarUrl() != null) {
                existing.setAvatarUrl(updateData.getAvatarUrl().trim());
            }

            boolean ok = studentDao.update(existing);
            if (ok) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(existing, "Student profile updated successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Failed to update student profile"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in StudentServlet PUT", e);
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
            JsonUtil.sendResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ApiResponse.error(400, "Missing or invalid student ID in path"));
            return;
        }

        try {
            Student existing = studentDao.getById(id);
            if (existing == null) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_NOT_FOUND, ApiResponse.error(404, "Student not found"));
                return;
            }

            boolean deleted = studentDao.delete(id);
            if (deleted) {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.ok(null, "Student deleted successfully"));
            } else {
                JsonUtil.sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(500, "Could not delete student"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in StudentServlet DELETE", e);
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
