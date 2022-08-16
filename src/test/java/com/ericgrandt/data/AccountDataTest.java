package com.ericgrandt.data;

import com.ericgrandt.TestUtils;
import com.ericgrandt.data.dto.AccountDto;
import com.ericgrandt.data.dto.VirtualAccountDto;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountDataTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void createAccount_WithSuccess_ShouldReturnTrue() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        boolean actual = sut.createAccount(UUID.randomUUID());

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createAccount_WithSqlException_ShouldLogExceptionAndReturnFalse() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        boolean actual = sut.createAccount(UUID.randomUUID());

        // Assert
        verify(loggerMock, times(1)).error(any(String.class));
        assertFalse(actual);
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
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("id")).thenReturn(accountId.toString());
        when(resultSetMock.getTimestamp("created")).thenReturn(timestamp);

        AccountData sut = new AccountData(loggerMock, databaseMock);

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
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        AccountDto actual = sut.getAccount(accountId);

        // Arrange
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getAccount_WithSqlException_ShouldLogExceptionAndReturnFalse() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        AccountDto actual = sut.getAccount(UUID.randomUUID());

        // Assert
        verify(loggerMock, times(1)).error(any(String.class));
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void createVirtualAccount_WithSuccess_ShouldReturnTrue() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        boolean actual = sut.createVirtualAccount("identifier");

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createVirtualAccount_WithSqlException_ShouldLogExceptionAndReturnFalse() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        boolean actual = sut.createVirtualAccount("identifier");

        // Assert
        verify(loggerMock, times(1)).error(any(String.class));
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getVirtualAccount_WithSuccess_ShouldReturnVirtualAccountDto() throws SQLException {
        // Arrange
        UUID id = UUID.randomUUID();
        String identifier = "virtualAccount";
        Timestamp timestamp = Timestamp.valueOf("2022-01-01 00:00:00");

        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("id")).thenReturn(id.toString());
        when(resultSetMock.getString("identifier")).thenReturn(identifier);
        when(resultSetMock.getTimestamp("created")).thenReturn(timestamp);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        VirtualAccountDto actual = sut.getVirtualAccount(identifier);
        VirtualAccountDto expected = new VirtualAccountDto(
            id.toString(),
            identifier,
            timestamp
        );

        // Arrange
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getVirtualAccount_WithNoVirtualAccountFound_ShouldReturnNull() throws SQLException {
        // Arrange
        String identifier = "invalidIdentifier";

        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        VirtualAccountDto actual = sut.getVirtualAccount(identifier);

        // Arrange
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getVirtualAccount_WithSqlException_ShouldLogExceptionAndReturnFalse() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        VirtualAccountDto actual = sut.getVirtualAccount("virtualAccount");

        // Assert
        verify(loggerMock, times(1)).error(any(String.class));
        assertNull(actual);
    }

    @Test
    @Tag("Integration")
    public void createAccount_ShouldCreateAnAccount() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        UUID uuid = UUID.randomUUID();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        sut.createAccount(uuid);

        AccountDto actual = getAccountForId(uuid);
        AccountDto expected = new AccountDto(
            uuid.toString(),
            null
        );

        // Assert
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertNotNull(actual.getCreated());
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
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(loggerMock, databaseMock);

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
    public void createVirtualAccount_ShouldCreateAVirtualAccount() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        String identifier = "virtualAccount";

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        sut.createVirtualAccount(identifier);

        VirtualAccountDto actual = getVirtualAccountForId(identifier);
        VirtualAccountDto expected = new VirtualAccountDto(
            null,
            identifier,
            null
        );

        // Assert
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getIdentifier(), actual.getIdentifier());
        assertNotNull(actual.getCreated());
    }

    @Test
    @Tag("Integration")
    public void getVirtualAccount_ShouldReturnAVirtualAccount() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedVirtualAccounts();

        String identifier = "virtualAccount2";

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        VirtualAccountDto actual = sut.getVirtualAccount(identifier);
        VirtualAccountDto expected = new VirtualAccountDto(
            "7cf27525-6491-4b3e-9208-ce232adf7c87",
            identifier,
            Timestamp.valueOf("2022-01-02 00:00:00")
        );

        // Assert
        assertEquals(expected, actual);
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

    private VirtualAccountDto getVirtualAccountForId(String identifier) throws SQLException {
        String query = "SELECT * FROM te_virtual_account WHERE identifier = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, identifier);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new VirtualAccountDto(
                            rs.getString("id"),
                            rs.getString("identifier"),
                            rs.getTimestamp("created")
                        );
                    }

                    return null;
                }
            }
        }
    }
}
