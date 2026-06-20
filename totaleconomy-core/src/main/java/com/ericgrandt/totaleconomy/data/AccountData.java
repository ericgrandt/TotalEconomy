package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.dto.CreateAccountRequest;
import com.ericgrandt.totaleconomy.model.TEAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountData {
    public TEAccount createAccount(Connection conn, CreateAccountRequest request) throws SQLException {
        var insertQuery = "INSERT INTO te_account(player_id, currency_code, balance) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, request.playerId().toString());
            stmt.setString(2, request.currencyCode());
            stmt.setBigDecimal(3, request.balance());
            stmt.executeUpdate();
        }
        return new TEAccount(request.playerId(), request.currencyCode(), request.balance());
    }

//    fun getAccount(
//        playerId: UUID,
//        currencyCode: String,
//    ): Result<TEAccount, Throwable> {
//        return runCatching {
//            AccountTable
//                .selectAll()
//                .where {
//                    (AccountTable.playerId eq playerId.toString()) and (AccountTable.currencyCode eq currencyCode)
//                }.single()
//                .toTEAccount()
//        }
//    }
}
