package com.ericgrandt.totaleconomy.api.infra;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionService {
    @FunctionalInterface
    interface Transaction<T> {
        T execute(Connection conn) throws SQLException;
    }

    <T> T runInTransaction(Transaction<T> transaction) throws SQLException;
}
