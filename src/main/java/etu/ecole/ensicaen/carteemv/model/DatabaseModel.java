package etu.ecole.ensicaen.carteemv.model;

import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseModel {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/log";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "";


    public void logToDatabase(String action, String response) throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             CallableStatement stmt = conn.prepareCall("{CALL InsertLog(?, ?)}")) {

            stmt.setString(1, action);
            stmt.setString(2, response);
            stmt.execute();
        }
    }
}
