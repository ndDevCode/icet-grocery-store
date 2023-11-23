package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade", "root", "1234");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DBConnection() {
    }

    public static DBConnection getInstance() throws SQLException, ClassNotFoundException {
        return dbConnection != null? dbConnection : (dbConnection = new DBConnection());
    }

    public static Connection getConnection() {
        return connection;
    }
}
