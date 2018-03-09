package com.erigitic.sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to compensate for the lack of proper SQL support in Sponge which results in TypedQueries not existing.
 *
 * Allows providing parameters by key and automatic insertion as good as possible.
 * For new serializations or corrections see {@link #serializeValue(Object)}
 *
 * @author MarkL4YG
 */
public class SqlQuery implements AutoCloseable {

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(:\\w+) ?");

    private DataSource dataSource;
    private String baseQuery;
    private Map<String, Object> parameters;

    private boolean closeConnection;
    private Connection connection;
    private PreparedStatement statement;

    private SqlQuery(String baseQuery) {
        this.baseQuery = baseQuery;
        this.parameters = new HashMap<>();
    }

    /**
     * Initialize the query with a {@link DataSource}
     * This will properly close the connection upon {@link #close()}
     */
    public SqlQuery(DataSource dataSource, String baseQuery) {
        this(baseQuery);
        this.dataSource = dataSource;
        this.closeConnection = true;
    }

    /**
     * Initialize the query with a {@link Connection}
     * This will only close the {@link Statement}
     */
    public SqlQuery(Connection connection, String baseQuery) {
        this(baseQuery);
        this.connection = connection;
        this.closeConnection = false;
    }

    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public void removeParameter(String key) {
        parameters.remove(key);
    }

    /**
     * Construct a statement from the base query and the parameters
     * @return A prepared statement
     * @throws SQLException When a parameter is missing in the parameter map.
     */
    public PreparedStatement getStatement() throws SQLException {
        if (statement != null) {
            return statement;
        }
        String fullQuery = baseQuery;
        Matcher matcher = PARAMETER_PATTERN.matcher(baseQuery);

        int start = 0;
        while (matcher.find(start)) {
            String group = matcher.group(1);
            String key = group.substring(1);
            Object value = parameters.getOrDefault(key, null);
            start = matcher.end(1);

            if (!parameters.containsKey(key)) {
                throw new SQLException("Unmatched parameter: " + group);
            }
            fullQuery = fullQuery.replaceAll(group, serializeValue(value));
        }

        if (connection == null) {
            connection = dataSource.getConnection();
        }
        statement = connection.prepareStatement(fullQuery);
        return statement;
    }

    /**
     * Serializes a value into an SQL representation.
     * @param value the value to serialize
     * @return the serializes String representation.
     */
    private String serializeValue(Object value) {
        if (value == null) {
            return "NULL";

        } else if (value.getClass().isArray()) {
            Object[] arr = (Object[]) value;
            StringBuilder result = new StringBuilder("(");

            for (int i = 0; i < arr.length; i++) {
                result.append(serializeValue(arr[i]));

                if (i != arr.length-1) {
                    result.append(",");
                }
            }
            return result.append(")").toString();

        } else if (value instanceof Set) {
            return serializeValue(((Set) value).toArray(new Object[((Set) value).size()]));

        } else {
            return "'" + value.toString() + "'";
        }
    }

    @Override
    /**
     * Closes all used resources.
     * Closes connection only when not provided by constructor.
     */
    public void close() throws SQLException {
        if (statement != null) {
            statement.close();
            statement = null;
        }
        // When initialized with a connection do not close it.
        if (closeConnection && connection != null) {
            connection.close();
            connection = null;
        }
    }
}
