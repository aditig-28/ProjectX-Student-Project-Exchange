package com.projectx.util;

import jakarta.servlet.http.HttpServletResponse;

public class CorsUtil {

    public static void applyCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    public static boolean handleOptions(HttpServletResponse response) {
        applyCors(response);
        response.setStatus(HttpServletResponse.SC_OK);
        return true;
    }
}
