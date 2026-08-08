package com.ericgrandt.totaleconomy.common.data;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SchemaInitializer {
    void init(Connection conn) throws SQLException;
}
