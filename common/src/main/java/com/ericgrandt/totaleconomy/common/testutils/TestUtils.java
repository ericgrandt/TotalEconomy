package com.ericgrandt.totaleconomy.common.testutils;

import com.ericgrandt.totaleconomy.common.data.SchemaInitializer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.UUID;

public class TestUtils {
    public static HikariDataSource startTestDb(boolean runInit, SchemaInitializer initializer) throws SQLException {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL");

        var dataSource = new HikariDataSource(config);
        if (runInit) {
            try (var conn = dataSource.getConnection()) {
                initializer.init(conn);
            }
        }

        return dataSource;
    }
}
