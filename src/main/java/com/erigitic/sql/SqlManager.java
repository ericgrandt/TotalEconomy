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
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

public class SqlManager {
    private Logger logger;
    public DataSource dataSource;
    private SqlService sql;

    public SqlManager(TotalEconomy totalEconomy, Logger logger) {
        this.logger = logger;

        try {
            dataSource = getDataSource("jdbc:" + totalEconomy.getDatabaseUrl() + "?user=" + totalEconomy.getDatabaseUser() + "&password=" + totalEconomy.getDatabasePassword());
        } catch (SQLException e) {
            logger.warn("Error getting data source!");
        } catch (UncheckedExecutionException e) {
            logger.warn("Error connecting to database! Check the config and make sure the database credentials are correct!");
        }
    }

    /**
     * Get the data source using the passed in JBDC url.
     *
     * @param jdbcUrl The JDBC url
     * @return DataSource
     * @throws SQLException Thrown when there's an exception getting the data source
     */
    public DataSource getDataSource(String jdbcUrl) throws SQLException {
        if (sql == null) {
            sql = Sponge.getServiceManager().provide(SqlService.class).get();
        }

        return sql.getDataSource(jdbcUrl);
    }

    /**
     * Create a new table in the database.
     *
     * @param tableName Name of the table to be created
     * @param cols The columns that the table should have
     * @return boolean Result of the query
     */
    public boolean createTable(String tableName, String cols) {
        try {
            Connection conn = dataSource.getConnection();

            boolean result =  conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" + cols + ")").execute();

            conn.close();

            return result;
        } catch (SQLException e) {
            logger.warn("[TE] An error occurred while creating a table!");
            e.printStackTrace();
        }

        return false;
    }
}
