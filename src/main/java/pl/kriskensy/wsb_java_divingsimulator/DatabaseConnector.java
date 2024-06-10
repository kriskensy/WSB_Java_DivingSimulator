package pl.kriskensy.wsb_java_divingsimulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class builds a connection to the database.
 */

public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost:3306/diving_simulator";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established!");

        } catch (SQLException e) {
            System.out.println("Connection do DB impossible.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Main method for testing the database connection.
     */

    public static void main(String[] args) {

        Connection connection = getConnection();
        if (connection != null) {

            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}