/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLQuery {
    private String statement;
    private DataSource dataSource;
    private ResultSet resultSet;

    private SQLQuery(Builder builder) {
        statement = builder.statement;
        dataSource = builder.dataSource;

        executeQuery();
    }

    public static SQLQuery.Builder builder(DataSource dataSource) {
        return new Builder(dataSource);
    }

    private void executeQuery() {
        try {
            Connection conn = dataSource.getConnection();

            Optional<ResultSet> resultSetOpt = Optional.of(conn.prepareStatement(statement).executeQuery());

            if (resultSetOpt.isPresent()) {
                resultSet = resultSetOpt.get();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean recordExists() {
        try {
            if (resultSet.isBeforeFirst())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean getBoolean() {
        try {
            if (resultSet.next())
                return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NullPointerException("Could not get boolean from ResultSet");
    }

    public static class Builder {
        private DataSource dataSource;
        private String statement = "";

        public Builder(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Builder select(String column) {
            statement += "SELECT " + column;

            return this;
        }

        public Builder from(String table) {
            statement += " FROM " + table;

            return this;
        }

        public Builder where(String comp) {
            statement += " WHERE " + comp;

            return this;
        }

        public Builder equals(String compVal) {
            statement += "='" + compVal + "'";

            return this;
        }

        public Builder and(String comp) {
            statement += " AND " + comp;

            return this;
        }

        public Builder insert(String table) {
            statement += "INSERT IGNORE INTO " + table;

            return this;
        }

        public Builder columns(String... columns) {
            String columnsJoined = String.join(",", columns);
            statement += " (" + columnsJoined + ")";

            return this;
        }

        public Builder values(String... values) {
            // Join all the values with a comma deliminator and surround with ()
            String valuesJoined = "('" + String.join("','", values) + "')";

            statement += "VALUES " + valuesJoined;

            return this;
        }

        public SQLQuery build() {
            return new SQLQuery(this);
        }
    }
}
