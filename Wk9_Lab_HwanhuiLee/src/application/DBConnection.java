package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_DRIVER =
            "com.mysql.cj.jdbc.Driver";

    private static final String DB_CONNECTION =
            "jdbc:mysql://localhost/hospital_db";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "class123";

    private DBConnection() {}

    public static Connection getDBConnection() throws SQLException {

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException exception) {
            throw new SQLException("SQL Driver not found", exception);
        }

        return DriverManager.getConnection(
                DB_CONNECTION,
                DB_USER,
                DB_PASSWORD
        );
    }
}