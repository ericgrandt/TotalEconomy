package com.ericgrandt.totaleconomy.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private final String url;
    private final String username;
    private final String password;
    private final DataSource dataSource;

    public Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.dataSource = createDataSource();
    }

    private HikariDataSource createDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl(this.url);
        config.setUsername(this.username);
        config.setPassword(this.password);

        config.setMinimumIdle(3);
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    public void initDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseBootstrapper.initSchema(conn);
            DatabaseBootstrapper.initData(conn);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
