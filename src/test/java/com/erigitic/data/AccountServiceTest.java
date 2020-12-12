package com.erigitic.data;

import com.erigitic.TestUtils;
import com.erigitic.domain.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("Integration")
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private AccountData sut;

    @Mock
    private Database databaseMock;

    @BeforeEach
    public void init() throws SQLException {
        TestUtils.resetDb();
        TestUtils.seedDb();

        sut = new AccountData(databaseMock);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());
    }

    @Test
    public void createAccount_WithValidData_ShouldInsertASingleAccountAndABalanceForBothCurrencies() throws SQLException {
        // Act
        sut.createAccount("ba64d376-8580-43b3-a3ee-2d6321114042");

        // Assert
        try (Connection conn = TestUtils.createTestConnection()) {
            assert conn != null;

            Statement userCountStmt = conn.createStatement();
            ResultSet userCount = userCountStmt.executeQuery(
                "SELECT COUNT(*) as rowcount FROM te_user\n" +
                    "WHERE id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            userCount.next();

            Statement balancesStmt = conn.createStatement();
            ResultSet balancesResultSet = balancesStmt.executeQuery(
                "SELECT * FROM te_balance\n" +
                    "WHERE user_id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            List<Balance> balances = new ArrayList<>();
            while (balancesResultSet.next()) {
                Balance balanceDto = new Balance(
                    balancesResultSet.getString("user_id"),
                    balancesResultSet.getInt("currency_id"),
                    balancesResultSet.getBigDecimal("balance")
                );
                balances.add(balanceDto);
            }

            int userCountResult = userCount.getInt("rowcount");
            int expectedUserCount = 1;
            List<Balance> expectedBalances = Arrays.asList(
                new Balance("ba64d376-8580-43b3-a3ee-2d6321114042", 1, BigDecimal.ZERO),
                new Balance("ba64d376-8580-43b3-a3ee-2d6321114042", 2, BigDecimal.ZERO)
            );

            assertEquals(userCountResult, expectedUserCount);
            assertEquals(balances.size(), 2);
            assertTrue(balances.containsAll(expectedBalances));
        }
    }

    // @Test
    // public void getAccount_WithValidUuid_ShouldReturnAnAccountWithTheCorrectUuid() {
    //     // Arrange
    //     String userId = "62694fb0-07cc-4396-8d63-4f70646d75f0";
    //
    //     // Act
    //     Optional<UniqueAccount> result = sut.getAccount(userId);
    //
    //     // Assert
    //     assertTrue(result.isPresent());
    //     assertEquals(result.get().getUniqueId(), UUID.fromString(userId));
    // }
}
