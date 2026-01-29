package vn.edu.hcmuaf.fit.demo1.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class DBContext {

    private static Properties dbProps = new Properties();

    static {
        try {
            InputStream input = DBContext.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                throw new RuntimeException("❌ Không tìm thấy file db.properties");
            }

            dbProps.load(input);
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {

        String host = dbProps.getProperty("db.host");
        String port = dbProps.getProperty("db.port");
        String dbName = dbProps.getProperty("db.dbname");
        String user = dbProps.getProperty("db.username");
        String pass = dbProps.getProperty("db.password");
        String option = dbProps.getProperty("db.option");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?" + option
                + "&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        return DriverManager.getConnection(url, user, pass);
    }
}
