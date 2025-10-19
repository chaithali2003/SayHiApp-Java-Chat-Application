package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import util.Constants;

public class DBConnection {
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                    Constants.DB_URL, 
                    Constants.DB_USER, 
                    Constants.DB_PASSWORD
                );
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}