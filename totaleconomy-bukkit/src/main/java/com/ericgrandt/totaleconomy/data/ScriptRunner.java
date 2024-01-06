package com.ericgrandt.totaleconomy.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ScriptRunner {
    private final Connection conn;

    private final String lineSep = System.lineSeparator();

    public ScriptRunner(Connection conn) {
        this.conn = conn;
    }

    public void runScript(Reader reader) throws IOException, SQLException {
        BufferedReader lineReader = new BufferedReader(reader);
        StringBuilder command = new StringBuilder();

        String line;
        while ((line = lineReader.readLine()) != null) {
            command.append(line);
            command.append(lineSep);

            if (line.contains(";")) {
                executeLine(command.toString());
                command.setLength(0);
            }
        }
    }

    private void executeLine(String command) throws SQLException {
        try (
            Statement statement = conn.createStatement()
        ) {
            statement.execute(command);
        }
    }
}