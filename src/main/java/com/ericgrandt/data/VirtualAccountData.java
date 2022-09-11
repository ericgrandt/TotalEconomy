package com.ericgrandt.data;

import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;

public class VirtualAccountData {
    private final Logger logger;
    private final Database database;

    public VirtualAccountData(Logger logger, Database database) {
        this.logger = logger;
        this.database = database;
    }

    public boolean createVirtualAccount(String identifier) {
        String createVirtualAccountQuery = "INSERT INTO te_virtual_account(identifier) VALUES (?)";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(createVirtualAccountQuery)
        ) {
            stmt.setString(1, identifier);
            stmt.execute();

            return true;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error creating virtual account (Query: %s, Parameters: %s)",
                    createVirtualAccountQuery,
                    identifier
                ),
                e
            );
        }

        return false;
    }

    public VirtualAccountDto getVirtualAccount(String identifier) {
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
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error getting virtual account (Query: %s, Parameters: %s)",
                    getVirtualAccountQuery,
                    identifier
                ),
                e
            );
        }

        return null;
    }

    public boolean deleteVirtualAccount(String identifier) {
        String deleteVirtualAccountQuery = "DELETE FROM te_virtual_account WHERE identifier = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(deleteVirtualAccountQuery)
        ) {
            stmt.setString(1, identifier);
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted != 0;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error deleting virtual account (Query: %s, Parameters: %s)",
                    deleteVirtualAccountQuery,
                    identifier
                ),
                e
            );
        }

        return false;
    }
}
