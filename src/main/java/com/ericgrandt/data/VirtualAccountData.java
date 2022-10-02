package com.ericgrandt.data;

import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VirtualAccountData {
    private final Database database;

    public VirtualAccountData(Database database) {
        this.database = database;
    }

    public int createVirtualAccount(String identifier) throws SQLException {
        String createVirtualAccountQuery = "INSERT INTO te_virtual_account(identifier) VALUES (?)";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(createVirtualAccountQuery)
        ) {
            stmt.setString(1, identifier);
            return stmt.executeUpdate();
        }
    }

    public VirtualAccountDto getVirtualAccount(String identifier) throws SQLException {
        String getVirtualAccountQuery = "SELECT * FROM te_virtual_account WHERE identifier = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getVirtualAccountQuery)
        ) {
            stmt.setString(1, identifier);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new VirtualAccountDto(
                        rs.getString("id"),
                        rs.getString("identifier"),
                        rs.getTimestamp("created")
                    );
                }
            }
        }

        return null;
    }

    public boolean deleteVirtualAccount(String identifier) throws SQLException {
        String deleteVirtualAccountQuery = "DELETE FROM te_virtual_account WHERE identifier = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(deleteVirtualAccountQuery)
        ) {
            stmt.setString(1, identifier);
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted != 0;
        }
    }
}
