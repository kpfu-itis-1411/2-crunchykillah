package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

public class DBConnection {
    private Stack<Connection> connectionPool = new Stack<>();
    private static DBConnection instance;

    private DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            for (int i = 0; i < 10; i++) {
                Connection connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/typing_game", "postgres", "root");
                connectionPool.push(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (!connectionPool.isEmpty()) {
            return connectionPool.pop();
        } else {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/typing_game", "postgres", "root");
        }
    }

    public synchronized void releaseConnection(Connection connection) {
        connectionPool.push(connection);
    }

    public void destroyConnection() {
        while (!connectionPool.isEmpty()) {
            try {
                Connection connection = connectionPool.pop();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}