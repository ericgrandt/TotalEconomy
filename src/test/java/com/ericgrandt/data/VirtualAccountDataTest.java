package com.ericgrandt.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.TestUtils;
import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VirtualAccountDataTest {
    @Test
    @Tag("Unit")
    public void createVirtualAccount_WithSuccess_ShouldReturnOne() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

        // Act
        int actual = sut.createVirtualAccount("identifier");
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
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

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

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

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

        // Act
        VirtualAccountDto actual = sut.getVirtualAccount(identifier);

        // Arrange
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void deleteVirtualAccount_WithRowDeleted_ShouldReturnTrue() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

        // Act
        boolean actual = sut.deleteVirtualAccount("identifier");

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void deleteVirtualAccount_WithNoRowsDeleted_ShouldReturnFalse() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0);

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

        // Act
        boolean actual = sut.deleteVirtualAccount("identifier");

        // Assert
        assertFalse(actual);
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

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

        // Act
        sut.createVirtualAccount(identifier);

        VirtualAccountDto actual = getVirtualAccountForIdentifier(identifier);
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

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

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

    @Test
    @Tag("Integration")
    public void deleteVirtualAccount_ShouldDeleteTheAccountAndReturnTrue() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedVirtualAccounts();

        String identifier = "virtualAccount1";

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        VirtualAccountData sut = new VirtualAccountData(databaseMock);

        // Act
        boolean actual = sut.deleteVirtualAccount(identifier);
        VirtualAccountDto deletedVirtualAccount = getVirtualAccountForIdentifier(identifier);

        // Assert
        assertTrue(actual);
        assertNull(deletedVirtualAccount);
    }

    private VirtualAccountDto getVirtualAccountForIdentifier(String identifier) throws SQLException {
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
