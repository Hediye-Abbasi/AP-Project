package com.shoppingmall.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized configuration for database connectivity.
 * <p>
 * This class encapsulates JDBC connection details, acting as a single source
 * of truth for the infrastructure layer. It follows SRP by only handling
 * connection creation and hides implementation details from repositories.
 */
public final class DatabaseConfig {

    private static final String URL = "jdbc:postgresql://localhost:5432/shopping_mall";
    private static final String USER = "postgres";
    private static final String PASSWORD = "8443";

    private DatabaseConfig() {
        // utility class
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


