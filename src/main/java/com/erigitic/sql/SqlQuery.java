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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;

public class SqlQuery {
    private String statement;
    private DataSource dataSource;
    private ResultSet resultSet;
    private int rowsAffected = 0;

    private SqlQuery(Builder builder) {
        statement = builder.statement;
        dataSource = builder.dataSource;

        if (builder.update) {
            executeUpdate();
        } else {
            executeQuery();
        }
    }

    public static SqlQuery.Builder builder(DataSource dataSource) {
        return new Builder(dataSource);
    }

    public void executeQuery() {
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

    /**
     * Executes statements that return an integer (update, insert, delete).
     *
     * @return int number of rows affected by the query
     */
    public int executeUpdate() {
        try {
            Connection conn = dataSource.getConnection();

            rowsAffected = conn.prepareStatement(statement).executeUpdate();

            conn.close();

            return rowsAffected;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Determines if a record was returned by an SQL query.
     *
     * @return boolean Does the record exist
     */
    public boolean recordExists() {
        try {
            if (resultSet.isBeforeFirst()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets a boolean from the executed SqlQuery. Throws a NPE when no boolean is returned.
     *
     * @return boolean value of column
     */
    public boolean getBoolean() {
        try {
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NullPointerException("[SQL] Could not retrieve boolean from database!");
    }

    /**
     * Gets a boolean from the executed SqlQuery. Returns a default value if no boolean is present.
     *
     * @param def default value to be returned
     * @return boolean value of column
     */
    public boolean getBoolean(boolean def) {
        try {
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * Gets an int from the executed SqlQuery. Throws a NPE when no int is returned.
     *
     * @return int value of column
     */
    public int getInt() {
        try {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NullPointerException("[SQL] Could not retrieve integer from database!");
    }

    /**
     * Gets an int from the executed SqlQuery. Returns a default value if no int is present.
     *
     * @param def default value to be returned
     * @return int value of column
     */
    public int getInt(int def) {
        try {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * Gets a BigDecimal from the executed SqlQuery. Throws a NPE when no BigDecimal is returned.
     *
     * @return BigDecimal value of column
     */
    public BigDecimal getBigDecimal() {
        try {
            if (resultSet.next()) {
                return resultSet.getBigDecimal(1).max(new BigDecimal(Double.MAX_VALUE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NullPointerException("[SQL] Could not retrieve BigDecimal from database!");
    }

    /**
     * Gets a BigDecimal from the executed SqlQuery. Returns a default value if no BigDecimal is present.
     *
     * @param def default value to be returned
     * @return BigDecimal value of column
     */
    public BigDecimal getBigDecimal(BigDecimal def) {
        try {
            if (resultSet.next()) {
                return resultSet.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * Gets a string from the executed SqlQuery. Throws a NPE when no string is returned.
     *
     * @return string value of column
     */
    public String getString() {
        try {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new NullPointerException("[SQL] Could not retrieve string from database!");
    }

    /**
     * Gets a string from the executed SqlQuery. Returns a default value if no string is present.
     *
     * @param def default value to be returned
     * @return string value of column
     */
    public String getString(String def) {
        try {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * Get the number of rows that were affected by a query.
     *
     * @return int Number of rows that were affected by the query
     */
    public int getRowsAffected() {
        return rowsAffected;
    }

    public static class Builder {
        private DataSource dataSource;
        private String statement = "";

        private boolean update = false;

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

        public Builder equals(String val) {
            statement += "='" + val + "'";

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
            // Join all the values with a comma deliminator and surround with ()
            String columnsJoined = " (" + String.join(",", columns) + ")";
            statement += columnsJoined;

            return this;
        }

        public Builder values(String... values) {
            String valuesJoined = "('" + String.join("','", values) + "')";

            statement += " VALUES " + valuesJoined;

            return this;
        }

        public Builder update(String table) {
            update = true;
            statement += "UPDATE " + table;

            return this;
        }

        public Builder set(String column) {
            statement += " SET " + column;

            return this;
        }

        public SqlQuery build() {
            return new SqlQuery(this);
        }
    }
}
