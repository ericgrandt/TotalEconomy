package com.erigitic.data;

import com.erigitic.TotalEconomy;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ShopData {
    private final TotalEconomy plugin;
    private final Logger logger;
    private final Database database;

    public ShopData(Database database) {
        this.plugin = TotalEconomy.getPlugin();
        this.logger = plugin.getLogger();
        this.database = database;
    }

    public void createShop(String shopName) {
        String createShopQuery = "INSERT INTO shop (id, shopName) VALUES (?, ?)";

        String uuid = UUID.randomUUID().toString();

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(createShopQuery)) {
                stmt.setString(1, uuid);
                stmt.setString(2, shopName);
                stmt.execute();
            }
        } catch (SQLException e) {
            logger.error(String.format("Error creating shop (Query: %s, Parameters: %s, %s)", createShopQuery, uuid, shopName));
        }
    }
}
