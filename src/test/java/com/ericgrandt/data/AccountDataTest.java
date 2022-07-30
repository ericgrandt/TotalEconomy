package com.ericgrandt.data;

import com.ericgrandt.TestUtils;
import com.ericgrandt.data.dto.AccountDto;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
    public void createAccount_WithSqlException_ShouldLogException() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        sut.createAccount(UUID.randomUUID());

        // Assert
        verify(loggerMock, times(1)).error(any(String.class));
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
        assertNotNull(actual.getCreated());
        assertEquals(expected.getId(), actual.getId());
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
