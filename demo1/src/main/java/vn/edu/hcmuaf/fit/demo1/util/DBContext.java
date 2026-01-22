package vn.edu.hcmuaf.fit.demo1.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {
    public Connection getConnection() throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Ten_Database_Cua_Ban;encrypt=false";
        String user = "sa";
        String pass = "Mat_Khau_Cua_Ban";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, user, pass);
    }

 
}