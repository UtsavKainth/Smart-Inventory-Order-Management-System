
package com.adbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";

    public static Connection getConnection() throws SQLException {

        String username = System.getenv("ADBMS_DB_USER");
        String password = System.getenv("ADBMS_DB_PASSWORD");

        if (username == null || password == null) {
            throw new SQLException(
                "Database environment variables are missing"
            );
        }

        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Oracle JDBC driver could not be loaded", e
            );
        }

        return DriverManager.getConnection(
            URL, username, password
        );
    }
}
