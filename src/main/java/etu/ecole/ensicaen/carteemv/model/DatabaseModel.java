package etu.ecole.ensicaen.carteemv.model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseModel {

    private static final Properties properties = new Properties();
    private static String JDBC_URL;
    private static String JDBC_USER;
    private static String JDBC_PASSWORD;

    static {
        try (InputStream input = DatabaseModel.class.getClassLoader().getResourceAsStream("databases.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find database.properties");
            }
            properties.load(input);
            JDBC_URL = properties.getProperty("jdbc.url");
            JDBC_USER = properties.getProperty("jdbc.user");
            JDBC_PASSWORD = properties.getProperty("jdbc.password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void logToDatabase(String action, String response) throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             CallableStatement stmt = conn.prepareCall("{CALL InsertLog(?, ?)}")) {

            stmt.setString(1, action);
            stmt.setString(2, response);
            stmt.execute();
        }
    }
}
