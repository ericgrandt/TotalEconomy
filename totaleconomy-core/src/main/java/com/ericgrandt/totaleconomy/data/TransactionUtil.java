package com.ericgrandt.totaleconomy.data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtil {
    private final DataSource dataSource;

    public TransactionUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @FunctionalInterface
    public interface Transaction<T> {
        T execute(Connection conn) throws SQLException;
    }

    public <T> T runInTransaction(Transaction<T> transaction) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = transaction.execute(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw e;
            }
        }
    }
}
