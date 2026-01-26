package vn.edu.hcmuaf.fit.demo1.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TicketDBContext {

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

 public static Connection getConnection() throws SQLException {
    String url =
        "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" +
        "databaseName=webjava;" +
        "encrypt=false";

    return DriverManager.getConnection(
        url,
        "sa",
        "123456"   // đúng password SQL Server
    );
}

}
