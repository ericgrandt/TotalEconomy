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
import java.sql.ResultSet;

public class SQLHandler {
    private DataSource dataSource;

    public SQLHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // SELECT {col} FROM {tableName};
    public ResultSet select(String tableName, String col) {

        return null;
    }

    // SELECT {col} FROM {tableName} WHERE {colArg}={val};
    public ResultSet select(String tableName, String col, String colArg, String val) {

        return null;
    }

    public boolean insert() {

        return false;
    }

    // DELETE FROM {tableName} WHERE {colArg}={val}
    public boolean delete(String tableName, String colArg, String val) {

        return false;
    }
}
