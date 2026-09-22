// Importing libraries to use in database connection file.
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// DatabaseConnection class
public class DatabaseConnection {
    // Parameters used to setup the db connection to the MySQL file.
    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String USER = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // Method used to connect to the sql file.
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        return conn;
    }

    // Main Method to test the connection to the db.
    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            System.out.println("Connected to the database");

            connection.close();
        } catch (SQLException e) {
            System.out.println("Database Connection Failed.");

            e.printStackTrace();
        }
    }
}
