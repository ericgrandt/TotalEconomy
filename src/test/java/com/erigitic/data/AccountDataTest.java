package com.erigitic.data;

import com.erigitic.TestUtils;
import com.erigitic.domain.Balance;
import com.erigitic.domain.TEAccount;
import com.erigitic.domain.TECurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

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
public class AccountDataTest {
    private AccountData sut;

    @Mock
    private Database databaseMock;

    @BeforeEach
    public void init() throws SQLException {
        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        sut = new AccountData(databaseMock);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());
    }

    @Test
    public void addAccount_WithValidData_ShouldInsertASingleAccountAndABalanceForBothCurrencies() throws SQLException {
        // Arrange
        TEAccount account = new TEAccount(
            UUID.fromString("ba64d376-8580-43b3-a3ee-2d6321114042"),
            "Display Name",
            null
        );

        // Act
        sut.addAccount(account);

        // Assert
        try (Connection conn = TestUtils.createTestConnection()) {
            assert conn != null;

            UUID uuid = UUID.fromString("ba64d376-8580-43b3-a3ee-2d6321114042");
            Statement userStmt = conn.createStatement();
            ResultSet userResult = userStmt.executeQuery(
                "SELECT COUNT(*) as rowcount, display_name FROM te_user\n" +
                    "WHERE id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            userResult.next();

            Statement balancesStmt = conn.createStatement();
            ResultSet balancesResultSet = balancesStmt.executeQuery(
                "SELECT * FROM te_balance\n" +
                    "WHERE user_id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            List<Balance> balances = new ArrayList<>();
            while (balancesResultSet.next()) {
                Balance balance = new Balance(
                    UUID.fromString(balancesResultSet.getString("user_id")),
                    balancesResultSet.getInt("currency_id"),
                    balancesResultSet.getBigDecimal("balance")
                );
                balances.add(balance);
            }

            int userCountResult = userResult.getInt("rowcount");
            String displayNameResult = userResult.getString("display_name");
            int expectedUserCount = 1;
            String expectedDisplayName = account.getDisplayName().toString();
            List<Balance> expectedBalances = Arrays.asList(
                new Balance(uuid, 1, BigDecimal.ZERO),
                new Balance(uuid, 2, BigDecimal.ZERO)
            );

            assertEquals(expectedUserCount, userCountResult);
            assertEquals(expectedDisplayName, displayNameResult);
            assertEquals(2, balances.size());
            assertTrue(balances.containsAll(expectedBalances));
        }
    }

    @Test
    public void getAccount_WithValidUuid_ShouldReturnTheCorrectAccount() throws SQLException {
        // Arrange
        TestUtils.seedUser();

        UUID userId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        String displayName = "Display Name";
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            new TECurrency(1, "Dollar", "Dollars", "$", true),
            BigDecimal.valueOf(123)
        );
        balances.put(
            new TECurrency(2, "Euro", "Euros", "E", false),
            BigDecimal.valueOf(456)
        );

        // Act
        Account result = sut.getAccount(userId);
        Account expectedResult = new TEAccount(
            userId,
            displayName,
            balances
        );

        // Assert
        assertNotNull(result);
        assertEquals(result, expectedResult);
    }

    @Test
    public void getBalance_WithValidUuidAndCurrencyId_ShouldReturnBalance() throws SQLException {
        // Arrange
        TestUtils.seedUser();

        UUID userId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        int currencyId = 1;

        // Act
        Balance result = sut.getBalance(userId, currencyId);
        Balance expectedResult = new Balance(
            userId,
            currencyId,
            BigDecimal.valueOf(123)
        );

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    public void getBalances_WithValidUuid_ShouldReturnAllBalances() {
        // Arrange
        TestUtils.seedUser();

        UUID userId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        // Act
        List<Balance> result = sut.getBalances(userId);
        List<Balance> expectedResult = Arrays.asList(
            new Balance(userId, 1, BigDecimal.valueOf(123)),
            new Balance(userId, 2, BigDecimal.valueOf(456))
        );

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidBalance_ShouldUpdateTheBalanceAndReturnIt() throws SQLException {
        // Arrange
        TestUtils.seedUser();

        Balance balance = new Balance(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"),
            1,
            BigDecimal.valueOf(1000)
        );

        // Act
        Balance result = sut.setBalance(balance);

        // Assert
        try (Connection conn = TestUtils.createTestConnection()) {
            assert conn != null;

            Statement balanceStmt = conn.createStatement();
            ResultSet balanceResult = balanceStmt.executeQuery(
                "SELECT balance\n" +
                    "FROM te_balance\n" +
                    "WHERE user_id='62694fb0-07cc-4396-8d63-4f70646d75f0' AND currency_id=1"
            );
            balanceResult.next();

            BigDecimal storedBalance = balanceResult.getBigDecimal("balance");
            BigDecimal expectedBalance = BigDecimal.valueOf(1000);

            assertEquals(expectedBalance, storedBalance);
            assertEquals(balance, result);
        }
    }

    @Test
    public void transfer_WithValidData_ShouldUpdateBothBalances() throws SQLException {
        // Arrange
        TestUtils.seedUsers();

        int currencyId = 1;
        Balance updatedFromBalance = new Balance(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"),
            currencyId,
            BigDecimal.valueOf(25)
        );
        Balance updatedToBalance = new Balance(
            UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea"),
            currencyId,
            BigDecimal.valueOf(125)
        );

        // Act
        boolean result = sut.setTransferBalances(updatedFromBalance, updatedToBalance);

        // Assert
        try (Connection conn = TestUtils.createTestConnection()) {
            assert conn != null;

            Statement fromBalanceStmt = conn.createStatement();
            ResultSet fromBalanceResult = fromBalanceStmt.executeQuery(
                "SELECT balance\n" +
                    "FROM te_balance\n" +
                    "WHERE user_id='62694fb0-07cc-4396-8d63-4f70646d75f0' AND currency_id=1"
            );
            fromBalanceResult.next();

            Statement toBalanceStmt = conn.createStatement();
            ResultSet toBalanceResult = toBalanceStmt.executeQuery(
                "SELECT balance\n" +
                    "FROM te_balance\n" +
                    "WHERE user_id='551fe9be-f77f-4bcb-81db-548db6e77aea' AND currency_id=1"
            );
            toBalanceResult.next();

            BigDecimal fromBalanceAmount = fromBalanceResult.getBigDecimal("balance");
            BigDecimal toBalanceAmount = toBalanceResult.getBigDecimal("balance");
            BigDecimal expectedFromBalanceAmount = BigDecimal.valueOf(25);
            BigDecimal expectedToBalanceAmount = BigDecimal.valueOf(125);

            assertEquals(expectedFromBalanceAmount, fromBalanceAmount);
            assertEquals(expectedToBalanceAmount, toBalanceAmount);
            assertTrue(result);
        }
    }
}
