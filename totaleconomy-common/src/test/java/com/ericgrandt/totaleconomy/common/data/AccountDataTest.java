package com.ericgrandt.totaleconomy.common.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.common.data.dto.BalanceDto;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountDataTest {
    @Test
    @Tag("Unit")
    public void createAccount_WithSuccess_ShouldReturnTrue() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1).thenReturn(1);

        AccountData sut = new AccountData(databaseMock);

        // Act
        boolean actual = sut.createAccount(UUID.randomUUID(), 1);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSqlException_ShouldRollback() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenThrow(SQLException.class);

        AccountData sut = new AccountData(databaseMock);

        // Act/Assert
        assertThrows(
            SQLException.class,
            () -> sut.createAccount(UUID.randomUUID(), 1)
        );
        verify(connectionMock, times(1)).rollback();
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithSuccess_ShouldReturnAccountDto() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Timestamp timestamp = Timestamp.valueOf("2022-01-01 00:00:00");

        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn(accountId.toString());
        when(resultSetMock.getTimestamp("created")).thenReturn(timestamp);

        AccountData sut = new AccountData(databaseMock);

        // Act
        AccountDto actual = sut.getAccount(accountId);
        AccountDto expected = new AccountDto(
            accountId.toString(),
            timestamp
        );

        // Arrange
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithNoAccountFound_ShouldReturnNull() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();

        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        AccountData sut = new AccountData(databaseMock);

        // Act
        AccountDto actual = sut.getAccount(accountId);

        // Arrange
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getAccounts_WithSuccess_ShouldReturnListOfAccounts() throws SQLException {
        // Arrange
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();

        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("id")).thenReturn(accountId1.toString()).thenReturn(accountId2.toString());
        when(resultSetMock.getTimestamp("created"))
            .thenReturn(Timestamp.valueOf("2022-01-01 00:00:00"))
            .thenReturn(Timestamp.valueOf("2022-01-01 00:00:00"));

        AccountData sut = new AccountData(databaseMock);

        // Act
        List<AccountDto> actual = sut.getAccounts();
        List<AccountDto> expected = Arrays.asList(
            new AccountDto(accountId1.toString(), Timestamp.valueOf("2022-01-01 00:00:00")),
            new AccountDto(accountId2.toString(), Timestamp.valueOf("2022-01-01 00:00:00"))
        );

        // Arrange
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deleteAccount_WithRowDeleted_ShouldReturnTrue() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        AccountData sut = new AccountData(databaseMock);

        // Act
        boolean actual = sut.deleteAccount(UUID.randomUUID());

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void deleteAccount_WithNoRowsDeleted_ShouldReturnFalse() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0);

        AccountData sut = new AccountData(databaseMock);

        // Act
        boolean actual = sut.deleteAccount(UUID.randomUUID());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Integration")
    public void createAccount_WithSuccess_ShouldCreateAnAccountAndBalance() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();

        UUID uuid = UUID.randomUUID();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(databaseMock);

        // Act
        sut.createAccount(uuid, 1);

        // Assert
        AccountDto actualAccount = getAccountForId(uuid);
        AccountDto expectedAccount = new AccountDto(
            uuid.toString(),
            null
        );
        assertNotNull(actualAccount);
        assertEquals(expectedAccount.id(), actualAccount.id());
        assertNotNull(actualAccount.created());

        BalanceDto actualBalance = TestUtils.getBalanceForAccountId(uuid, 1);
        BalanceDto expectedBalance = new BalanceDto(
            null,
            uuid.toString(),
            1,
            BigDecimal.valueOf(100.50).setScale(2, RoundingMode.DOWN)
        );
        assertNotNull(actualBalance);
        assertNotNull(actualBalance.id());
        assertEquals(expectedBalance.accountId(), actualBalance.accountId());
        assertEquals(expectedBalance.currencyId(), actualBalance.currencyId());
        assertEquals(expectedBalance.balance(), actualBalance.balance());
    }

    @Test
    @Tag("Integration")
    public void getAccount_ShouldReturnAnAccount() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        UUID uuid = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(databaseMock);

        // Act
        AccountDto actual = sut.getAccount(uuid);
        AccountDto expected = new AccountDto(
            uuid.toString(),
            Timestamp.valueOf("2022-01-01 00:00:00")
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getAccounts_ShouldReturnListOfAccounts() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(databaseMock);

        // Act
        List<AccountDto> actual = sut.getAccounts();
        List<AccountDto> expected = Arrays.asList(
            new AccountDto("62694fb0-07cc-4396-8d63-4f70646d75f0", Timestamp.valueOf("2022-01-01 00:00:00")),
            new AccountDto("551fe9be-f77f-4bcb-81db-548db6e77aea", Timestamp.valueOf("2022-01-02 00:00:00"))
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void deleteAccount_ShouldDeleteTheAccountAndReturnTrue() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        UUID uuid = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(databaseMock);

        // Act
        boolean actual = sut.deleteAccount(uuid);
        AccountDto deletedAccount = getAccountForId(uuid);

        // Assert
        assertTrue(actual);
        assertNull(deletedAccount);
    }

    private AccountDto getAccountForId(UUID uuid) throws SQLException {
        String query = "SELECT * FROM te_account WHERE id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new AccountDto(
                            rs.getString("id"),
                            rs.getTimestamp("created")
                        );
                    }

                    return null;
                }
            }
        }
    }
}