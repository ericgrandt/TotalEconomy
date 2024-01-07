package com.ericgrandt.totaleconomy.common.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class DatabaseTest {
    @Test
    @Tag("Integration")
    public void getConnection_ShouldCreateDatabaseConnection() throws SQLException {
        // Arrange
        Database sut = new Database("jdbc:h2:mem:totaleconomy", "", "");
        String query = "SELECT 1";

        // Act/Assert
        try (
            Connection conn = sut.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            rs.next();
            int actual = rs.getInt(1);
            int expected = 1;

            assertEquals(expected, actual);
        }
    }

    @Test
    @Tag("Integration")
    public void initDatabase_ShouldInitializeDatabase() throws SQLException, IOException {
        // Arrange
        Database sut = new Database("jdbc:h2:mem:test;MODE=MySQL", "", "");

        // Act
        sut.initDatabase();

        // Assert
        try (
            Connection conn = sut.getDataSource().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES")
        ) {
            List<String> actual = new ArrayList<>();

            while (rs.next()) {
                actual.add(rs.getString(1).toLowerCase());
            }

            List<String> expected = List.of(
                "te_account",
                "te_balance",
                "te_currency",
                "te_default_balance",
                "te_job",
                "te_job_action",
                "te_job_experience",
                "te_job_reward"
            );

            assertEquals(expected, actual);
        }
    }
}
