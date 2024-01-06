package com.ericgrandt.totaleconomy.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ericgrandt.totaleconomy.TestUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ScriptRunnerTest {
    @Test
    @Tag("Integration")
    public void runScript_ShouldRunScript() throws SQLException, IOException {
        // Arrange
        InputStream is = getClass().getResourceAsStream("/test.sql");
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(is));
        ScriptRunner sut = new ScriptRunner(TestUtils.getConnection());

        // Act
        sut.runScript(reader);

        // Assert
        try (
            Connection conn = TestUtils.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES")
        ) {
            List<String> actual = new ArrayList<>();

            while (rs.next()) {
                actual.add(rs.getString(1).toLowerCase());
            }

            assertTrue(actual.contains("test_table_one"));
            assertTrue(actual.contains("test_table_two"));
        }
    }
}
