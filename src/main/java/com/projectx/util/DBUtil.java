package com.projectx.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil {

    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());

    private static String dbDriver = "oracle.jdbc.OracleDriver";
    private static String dbUrl = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static String dbUsername = "PROJECTX";
    private static String dbPassword = "projectx123";
    private static boolean driverLoaded = false;
    private static String initError = null;

    static {
        loadConfiguration();
        try {
            Class.forName(dbDriver);
            driverLoaded = true;
            LOGGER.info("Oracle JDBC Driver registered successfully: " + dbDriver);
        } catch (ClassNotFoundException e) {
            initError = "Oracle JDBC Driver not found in classpath: " + e.getMessage();
            LOGGER.log(Level.SEVERE, initError, e);
        }
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                dbDriver = props.getProperty("db.driver", dbDriver);
                dbUrl = props.getProperty("db.url", dbUrl);
                dbUsername = props.getProperty("db.username", dbUsername);
                dbPassword = props.getProperty("db.password", dbPassword);
                LOGGER.info("Loaded database configuration from classpath db.properties");
            } else {
                LOGGER.warning("db.properties not found in classpath; using default fallback configuration.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error reading db.properties, relying on environment or defaults: " + e.getMessage());
        }

        // Support environment variable overrides (Twelve-Factor App best practice)
        String envUrl = System.getenv("PROJECTX_DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            dbUrl = envUrl.trim();
        }

        String envUser = System.getenv("PROJECTX_DB_USER");
        if (envUser != null && !envUser.trim().isEmpty()) {
            dbUsername = envUser.trim();
        }

        String envPass = System.getenv("PROJECTX_DB_PASSWORD");
        if (envPass != null && !envPass.trim().isEmpty()) {
            dbPassword = envPass.trim();
        }

        // Support JVM system property overrides (-Ddb.url=...)
        String sysUrl = System.getProperty("db.url");
        if (sysUrl != null && !sysUrl.trim().isEmpty()) {
            dbUrl = sysUrl.trim();
        }

        String sysUser = System.getProperty("db.username");
        if (sysUser != null && !sysUser.trim().isEmpty()) {
            dbUsername = sysUser.trim();
        }

        String sysPass = System.getProperty("db.password");
        if (sysPass != null && !sysPass.trim().isEmpty()) {
            dbPassword = sysPass.trim();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("Oracle JDBC Driver failed to load: " + initError);
        }
        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to Oracle Database at " + dbUrl + " as " + dbUsername + ": " + e.getMessage());
            throw new SQLException(
                "Unable to establish Oracle Database connection (" + e.getMessage() + "). " +
                "Check that Oracle XE service is running, listener is active on port 1521, and credentials in db.properties are correct.",
                e
            );
        }
    }

    public static Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        result.put("driverClass", dbDriver);
        result.put("jdbcUrl", dbUrl);
        result.put("dbUser", dbUsername);

        try (Connection conn = getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            result.put("connected", true);
            result.put("databaseProductName", meta.getDatabaseProductName());
            result.put("databaseProductVersion", meta.getDatabaseProductVersion());
            result.put("driverName", meta.getDriverName());
            result.put("driverVersion", meta.getDriverVersion());
            result.put("message", "Connected successfully to Oracle Database!");
        } catch (Exception e) {
            result.put("connected", false);
            result.put("error", e.getMessage());
            result.put("message", "Failed to connect to Oracle Database.");
        }
        return result;
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables == null) return;
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static String getDbUrl() { return dbUrl; }
    public static String getDbUsername() { return dbUsername; }
}
