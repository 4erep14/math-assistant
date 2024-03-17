package org.example.repository;

import org.example.validators.MathEquationValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = System.getenv("DB_LINK");
    private static final String USER = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    static {
        try {
            createTablesIfNotExist();
        } catch (SQLException e) {
            System.out.println("Something went wrong");
        }
    }

    private static void createTablesIfNotExist() throws SQLException {
        String createEquationsTable = "CREATE TABLE IF NOT EXISTS equations (" +
                "id SERIAL PRIMARY KEY," +
                "equation VARCHAR(255) UNIQUE" +
                ")";

        String createRootsTable = "CREATE TABLE IF NOT EXISTS equation_roots (" +
                "equation_id INTEGER REFERENCES equations(id)," +
                "root NUMERIC," +
                "PRIMARY KEY (equation_id, root)" +
                ")";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(createEquationsTable);
            statement.executeUpdate(createRootsTable);
        }
    }

    public static void saveEquation(String equation) throws SQLException {
        if (!MathEquationValidator.isValidEquation(equation)) {
            throw new IllegalArgumentException("Invalid equation syntax");
        }
        String sql = "INSERT INTO equations (equation) VALUES (?) ON CONFLICT (equation) DO NOTHING";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, equation);
            preparedStatement.executeUpdate();
        }
    }

    public static void saveRoot(String equation, Double root) throws SQLException {
        String sql = "INSERT INTO equation_roots (equation_id, root) VALUES (" +
                "(SELECT id FROM equations WHERE equation = ?), ?) " +
                "ON CONFLICT (equation_id, root) DO NOTHING";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (root == null) {
                throw new IllegalArgumentException("Root value cannot be null");
            }

            preparedStatement.setString(1, equation);
            preparedStatement.setDouble(2, root);
            preparedStatement.executeUpdate();
        }
    }

    public static List<String> findEquationsByRoots(List<Double> roots) throws SQLException {
        List<String> equations = new ArrayList<>();
        String sql = "SELECT DISTINCT e.equation " +
                "FROM equations e " +
                "JOIN equation_roots er ON e.id = er.equation_id " +
                "WHERE er.root = ANY(?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Array rootsArray = connection.createArrayOf("NUMERIC", roots.toArray());
            preparedStatement.setArray(1, rootsArray);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                equations.add(resultSet.getString("equation"));
            }
        }
        return equations;
    }

    public static List<String> findUnsolvedEquations() throws SQLException {
        List<String> equations = new ArrayList<>();
        String sql = "SELECT equation FROM equations e " +
                "WHERE NOT EXISTS (SELECT 1 FROM equation_roots er WHERE er.equation_id = e.id)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                equations.add(resultSet.getString("equation"));
            }
        }
        return equations;
    }
}
