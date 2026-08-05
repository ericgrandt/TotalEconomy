package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.api.infra.TransactionService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtil implements TransactionService {
    private final DataSource dataSource;

    public TransactionUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
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
