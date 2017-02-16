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

import com.erigitic.main.TotalEconomy;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLHandler {
    private TotalEconomy totalEconomy;
    private Logger logger;
    public DataSource dataSource;
    private SqlService sql;
    private final String dbName = "totaleconomy";

    public SQLHandler(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        try {
            dataSource = getDataSource(totalEconomy.getDatabaseUrl() + "?user=" + totalEconomy.getDatabaseUser() + "&password=" + totalEconomy.getDatabasePassword());
        } catch (SQLException e) {
            logger.warn("Error getting data source!");
        } catch (UncheckedExecutionException e) {
            logger.warn("Could not connect to database! Make sure database information is correct in totaleconomy.conf!");
        }
    }

    /**
     * Get the data source using the passed in JBDC url
     *
     * @param jdbcUrl
     * @return DataSource
     * @throws SQLException
     */
    public DataSource getDataSource(String jdbcUrl) throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }

        return sql.getDataSource(jdbcUrl);
    }

    public boolean createDatabase() {
        try {
            Connection conn = dataSource.getConnection();

            boolean result = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS totaleconomy").execute();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("Error creating database!");
            e.printStackTrace();
        }

        return false;
    }

    public boolean createTable(String tableName, String cols) {
        try {
            Connection conn = dataSource.getConnection();

            boolean result =  conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + dbName + "." + tableName + " (" + cols + ")").execute();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("Error creating table!");
            e.printStackTrace();
        }

        return false;
    }

    public Optional<ResultSet> select(String col, String tableName) {
        try {
            Connection conn = dataSource.getConnection();

            Optional<ResultSet> resultSetOpt = Optional.of(conn.prepareStatement("SELECT " + col + " FROM " + dbName + "." + tableName).executeQuery());

            conn.close();

            return resultSetOpt;
        } catch (SQLException e) {
            logger.warn("Error selecting column!");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<ResultSet> select(String col, String tableName, String colComp, String valComp) {
        try {
            Connection conn = dataSource.getConnection();

            Optional<ResultSet> resultSetOpt = Optional.of(conn.prepareStatement("SELECT " + col + " FROM " + dbName + "." + tableName + " WHERE " + colComp + "='" + valComp + "'").executeQuery());

            conn.close();

            return resultSetOpt;
        } catch (SQLException e) {
            logger.warn("Error selecting column with comparator!");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public int insert(String tableName, String[] colsArray, String[] valsArray) {
        try {
            Connection conn = dataSource.getConnection();

            String cols = String.join(",", colsArray);
            String vals = String.join(",", valsArray);

            int result = conn.prepareStatement("INSERT IGNORE INTO " + dbName + "." + tableName + " (" + cols + ") VALUES " + "(" + vals + ")").executeUpdate();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("Error inserting new record!");
            e.printStackTrace();
        }

        return 0;
    }

    public int insert(String tableName, String col, String val) {
        try {
            Connection conn = dataSource.getConnection();

            int result = conn.prepareStatement("INSERT IGNORE INTO " + dbName + "." + tableName + " (" + col + ") VALUES " + "(" + val + ")").executeUpdate();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("Error inserting new record!");
            e.printStackTrace();
        }

        return 0;
    }

    public int update(String tableName, String col, String val, String colComp, String valComp) {
        try {
            Connection conn = dataSource.getConnection();

            int result =  conn.prepareStatement("UPDATE " + dbName + "." + tableName + " SET " + col + "='" + val + "' WHERE " + colComp + "='" + valComp + "'").executeUpdate();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("Error updating column!");
            e.printStackTrace();
        }

        return 0;
    }

    public int delete(String tableName, String colComp, String valComp) {
        try {
            Connection conn = dataSource.getConnection();

            int result =  conn.prepareStatement("DELETE FROM " + dbName + "." + tableName + " WHERE " + colComp + "='" + valComp + "'").executeUpdate();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("Error deleting from table!");
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Checks a {@link ResultSet} for records. If the ResultSet is empty, this function will return false, otherwise it
     * will return true.
     *
     * @param rs the ResultSet that is being checked
     * @return A boolean stating if the ResultSet is empty or not
     */
    public boolean recordExists(ResultSet rs) {
        try {
            if (rs.isBeforeFirst())
                return true;
        } catch (SQLException e) {
            logger.warn("Error when checking for existence of record!");
            e.printStackTrace();
        }

        return false;
    }

    public void close(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
