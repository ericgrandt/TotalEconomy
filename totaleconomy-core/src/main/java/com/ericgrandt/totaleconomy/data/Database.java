package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.config.TEConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Database implements DataSourceProvider {
    private final DataSource dataSource;

    public Database(String url, String username, String password) {
        this.dataSource = createDataSource(url, username, password);
    }

    private HikariDataSource createDataSource(String url, String username, String password) {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMinimumIdle(3);
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    public void initDatabase(TEConfig config) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseBootstrapper.initSchema(conn);
            DatabaseBootstrapper.initData(conn, config);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
