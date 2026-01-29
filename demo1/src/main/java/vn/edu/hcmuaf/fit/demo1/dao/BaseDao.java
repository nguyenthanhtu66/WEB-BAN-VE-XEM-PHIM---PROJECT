package vn.edu.hcmuaf.fit.demo1.dao;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.jdbi.v3.core.Jdbi;
import com.mysql.cj.jdbc.Driver;    

import java.sql.SQLException;

public class BaseDao {
    private Jdbi jdbi;

    public Jdbi get(){
        if(jdbi==null) connect();
        return jdbi;
    }

    private void connect() {
        MysqlDataSource dataSource = new MysqlDataSource();
        System.out.println("jdbc:mysql://" + DBProperties.host() + ":" + DBProperties.port() + "/" + DBProperties.dbname() + "?" + DBProperties.option());
        dataSource.setURL("jdbc:mysql://" + DBProperties.host() + ":" + DBProperties.port() + "/" + DBProperties.dbname() + "?" + DBProperties.option());
        dataSource.setUser(DBProperties.username());
        dataSource.setPassword(DBProperties.password());
        try {
            dataSource.setUseCompression(true);
            dataSource.setAutoReconnect(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        jdbi = Jdbi.create(dataSource);
    }

}
