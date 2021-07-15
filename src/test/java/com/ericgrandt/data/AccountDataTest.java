package com.ericgrandt.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.TestUtils;
import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TEAccount;
import com.ericgrandt.domain.TECurrency;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
@ExtendWith(MockitoExtension.class)
public class AccountDataTest {
    private AccountData sut;

    @Mock
    private Database databaseMock;

    @Mock
    private Logger loggerMock;

    @BeforeEach
    public void init(TestInfo info) throws SQLException {
        sut = new AccountData(loggerMock, databaseMock);

        if (info.getTags().contains("Unit")) {
            return;
        }

        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());
    }

    @Test
    @Tag("Unit")
    public void addAccount_WithSQLException_ShouldCatchSQLException() throws SQLException {
        TEAccount account = mock(TEAccount.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        sut.addAccount(account);

        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithSQLException_ShouldReturnFalse() throws SQLException {
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        boolean result = sut.hasAccount(UUID.randomUUID());

        assertFalse(result);
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithEmptyResultSet_ShouldReturnFalse() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        boolean result = sut.hasAccount(UUID.randomUUID());

        assertFalse(result);
        verify(loggerMock, times(0)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithSQLException_ShouldReturnNull() throws SQLException {
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        UniqueAccount result = sut.getAccount(UUID.randomUUID());

        assertNull(result);
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithSQLException_ShouldReturnNull() throws SQLException {
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        Balance result = sut.getBalance(UUID.randomUUID(), 0);

        assertNull(result);
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void getBalance_WithEmptyResultSet_ShouldReturnFalse() throws SQLException {
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        Balance result = sut.getBalance(UUID.randomUUID(), 0);

        assertNull(result);
        verify(loggerMock, times(0)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void getBalances_WithSQLException_ShouldReturnEmptyList() throws SQLException {
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        List<Balance> result = sut.getBalances(UUID.randomUUID());

        assertTrue(result.isEmpty());
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void setBalance_WithSQLException_ShouldReturnNull() throws SQLException {
        Balance balance = new Balance(
            UUID.randomUUID(),
            0,
            BigDecimal.ZERO
        );
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        Balance result = sut.setBalance(balance);

        assertNull(result);
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void setTransferBalances_WithSQLExceptionOnConnection_ShouldReturnFalse() throws SQLException {
        Balance fromBalance = new Balance(
            UUID.randomUUID(),
            0,
            BigDecimal.ZERO
        );
        Balance toBalance = new Balance(
            UUID.randomUUID(),
            0,
            BigDecimal.ZERO
        );
        Connection connectionMock = mock(Connection.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(any(String.class))).thenThrow(SQLException.class);

        boolean result = sut.setTransferBalances(fromBalance, toBalance);

        assertFalse(result);
        verify(connectionMock, times(1)).rollback();
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void setTransferBalances_WithSQLExceptionOnQueryRun_ShouldReturnNull() throws SQLException {
        Balance fromBalance = new Balance(
            UUID.randomUUID(),
            0,
            BigDecimal.ZERO
        );
        Balance toBalance = new Balance(
            UUID.randomUUID(),
            0,
            BigDecimal.ZERO
        );
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        boolean result = sut.setTransferBalances(fromBalance, toBalance);

        assertFalse(result);
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Integration")
    public void addAccount_WithValidData_ShouldInsertASingleAccountAndABalanceForBothCurrencies() throws SQLException {
        // Arrange
        UUID uuid = UUID.fromString("ba64d376-8580-43b3-a3ee-2d6321114042");
        TEAccount account = new TEAccount(
            uuid,
            "Display Name",
            null
        );

        // Act
        sut.addAccount(account);

        // Assert
        try (Connection conn = TestUtils.getConnection()) {
            assert conn != null;

            Statement countStmt = conn.createStatement();
            ResultSet countResult = countStmt.executeQuery(
                "SELECT COUNT(1) as rowcount FROM te_user\n"
                    + "WHERE id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            countResult.next();

            Statement userStmt = conn.createStatement();
            ResultSet userResult = userStmt.executeQuery(
                "SELECT display_name FROM te_user\n"
                    + "WHERE id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            userResult.next();

            Statement balancesStmt = conn.createStatement();
            ResultSet balancesResultSet = balancesStmt.executeQuery(
                "SELECT * FROM te_balance\n"
                    + "WHERE user_id='ba64d376-8580-43b3-a3ee-2d6321114042'"
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

            int userCountResult = countResult.getInt("rowcount");
            String displayNameResult = userResult.getString("display_name");
            int expectedUserCount = 1;
            String expectedDisplayName = PlainComponentSerializer.plain().serialize(account.displayName());
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
    @Tag("Integration")
    public void hasAccount_WithValidUuid_ShouldReturnTrue() {
        // Arrange
        TestUtils.seedUser();

        UUID userId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        // Act
        boolean result = sut.hasAccount(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    @Tag("Integration")
    public void hasAccount_WithInvalidUuid_ShouldReturnTrue() {
        // Arrange
        TestUtils.seedUser();

        UUID userId = UUID.fromString("12345678-07cc-4396-8d63-4f70646d75f0");

        // Act
        boolean result = sut.hasAccount(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    @Tag("Integration")
    public void getAccount_WithValidUuid_ShouldReturnTheCorrectAccount() {
        // Arrange
        TestUtils.seedUser();

        UUID userId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        String displayName = "Display Name";
        Map<Currency, BigDecimal> balances = new HashMap<>();
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
    @Tag("Integration")
    public void getBalance_WithValidUuidAndCurrencyId_ShouldReturnBalance() {
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
    @Tag("Integration")
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
    @Tag("Integration")
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
        try (Connection conn = TestUtils.getConnection()) {
            assert conn != null;

            Statement balanceStmt = conn.createStatement();
            ResultSet balanceResult = balanceStmt.executeQuery(
                "SELECT balance\n"
                    + "FROM te_balance\n"
                    + "WHERE user_id='62694fb0-07cc-4396-8d63-4f70646d75f0' AND currency_id=1"
            );
            balanceResult.next();

            BigDecimal storedBalance = balanceResult.getBigDecimal("balance");
            BigDecimal expectedBalance = BigDecimal.valueOf(1000);

            assertEquals(expectedBalance, storedBalance);
            assertEquals(balance, result);
        }
    }

    @Test
    @Tag("Integration")
    public void setTransferBalances_WithValidData_ShouldUpdateBothBalances() throws SQLException {
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
        try (Connection conn = TestUtils.getConnection()) {
            assert conn != null;

            Statement fromBalanceStmt = conn.createStatement();
            ResultSet fromBalanceResult = fromBalanceStmt.executeQuery(
                "SELECT balance\n"
                    + "FROM te_balance\n"
                    + "WHERE user_id='62694fb0-07cc-4396-8d63-4f70646d75f0' AND currency_id=1"
            );
            fromBalanceResult.next();

            Statement toBalanceStmt = conn.createStatement();
            ResultSet toBalanceResult = toBalanceStmt.executeQuery(
                "SELECT balance\n"
                    + "FROM te_balance\n"
                    + "WHERE user_id='551fe9be-f77f-4bcb-81db-548db6e77aea' AND currency_id=1"
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
