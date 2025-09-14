package com.ericgrandt.totaleconomy.common.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.ibatis.jdbc.ScriptRunner;

public class Database {
    private final String url;
    private final String user;
    private final String password;
    private final HikariDataSource dataSource;

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.dataSource = createDataSource();
    }

    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.url);
        config.setUsername(this.user);
        config.setPassword(this.password);
        config.addDataSourceProperty("minimumIdle", "3");
        config.addDataSourceProperty("maximumPoolSize", "10");
        return new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public void initDatabase() throws SQLException, IOException {
        try (
            InputStream is = getClass().getResourceAsStream("/schema.sql");
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(is));
            Connection conn = getDataSource().getConnection();
        ) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.runScript(reader);
        }
    }
}
