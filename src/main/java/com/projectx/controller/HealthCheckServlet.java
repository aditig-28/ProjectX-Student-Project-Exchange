package com.projectx.controller;

import com.projectx.model.ApiResponse;
import com.projectx.util.CorsUtil;
import com.projectx.util.DBUtil;
import com.projectx.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "HealthCheckServlet", urlPatterns = {"/api/health"})
public class HealthCheckServlet extends HttpServlet {

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        CorsUtil.handleOptions(resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CorsUtil.applyCors(resp);
        Map<String, Object> test = DBUtil.testConnection();
        boolean connected = Boolean.TRUE.equals(test.get("connected"));
        int status = connected ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE;

        JsonUtil.sendResponse(resp, status, ApiResponse.ok(test, connected ? "System and Oracle Database are fully operational" : "Database connection check failed"));
    }
}
