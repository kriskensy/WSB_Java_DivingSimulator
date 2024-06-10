package pl.kriskensy.wsb_java_divingsimulator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides helper methods to interact with the database.
 */

public class DatabaseHelper {

    /**
     * Retrieves a list of students from the database.
     */

    public static List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM students";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("No");
                    String name = resultSet.getString("Name");
                    String experience = resultSet.getString("Experience");
                    int tankSize = resultSet.getInt("Tank_size");
                    students.add(new Student(id, name, experience, tankSize));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
