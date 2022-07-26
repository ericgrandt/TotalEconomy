package com.ericgrandt.data;

import com.ericgrandt.impl.UniqueAccountImpl;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.sql.SQLException;

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
        UniqueAccount accountMock = mock(UniqueAccountImpl.class);
        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        AccountData sut = new AccountData(loggerMock, databaseMock);

        // Act
        sut.createAccount(accountMock);

        // Assert
        verify(loggerMock, times(1)).error(any(String.class));
    }
}
