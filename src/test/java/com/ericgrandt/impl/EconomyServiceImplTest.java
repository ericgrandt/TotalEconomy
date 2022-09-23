package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.VirtualAccountData;
import com.ericgrandt.data.dto.AccountDto;
import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EconomyServiceImplTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void hasAccount_WithAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        AccountDto account = mock(AccountDto.class);
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(account);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        boolean actual = sut.hasAccount(UUID.randomUUID());

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithNoAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(null);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        boolean actual = sut.hasAccount(UUID.randomUUID());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCaughtSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(any(UUID.class))).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        boolean actual = sut.hasAccount(UUID.randomUUID());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCaughtSqlException_ShouldLogError() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(any(UUID.class))).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.hasAccount(uuid);

        // Assert
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithVirtualAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        VirtualAccountDto virtualAccount = mock(VirtualAccountDto.class);
        when(virtualAccountDataMock.getVirtualAccount(any(String.class))).thenReturn(virtualAccount);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        boolean actual = sut.hasAccount("random");

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithNoVirtualAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        when(virtualAccountDataMock.getVirtualAccount(any(String.class))).thenReturn(null);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        boolean actual = sut.hasAccount("random");

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasVirtualAccount_WithCaughtSqlException_ShouldLogError() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        when(virtualAccountDataMock.getVirtualAccount(any(String.class))).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        sut.hasAccount("random");

        // Assert
        verify(loggerMock, times(1)).error(any(String.class), any(SQLException.class));
    }

    // @Test
    // @Tag("Unit")
    // public void defaultCurrency_ShouldReturnCurrency() {
    //     // Arrange
    //     CurrencyData currencyDataMock = mock(CurrencyData.class);
    //     Currency currencyMock = mock(TECurrency.class);
    //     when(currencyDataMock.getDefaultCurrency()).thenReturn(currencyMock);
    //
    //     EconomyService sut = new EconomyServiceImpl(currencyDataMock, null);
    //
    //     // Act
    //     Currency actual = sut.defaultCurrency();
    //
    //     // Assert
    //     assertEquals(currencyMock, actual);
    // }
    //
    // @Test
    // @Tag("Unit")
    // public void hasAccount_WithUUID_ShouldReturnBoolean() {
    //     // Arrange
    //     AccountData accountDataMock = mock(AccountData.class);
    //     UUID uuid = UUID.randomUUID();
    //     when(accountDataMock.hasAccount(uuid)).thenReturn(true);
    //
    //     EconomyService sut = new EconomyServiceImpl(null, accountDataMock);
    //
    //     // Act
    //     boolean actual = sut.hasAccount(uuid);
    //
    //     // Assert
    //     assertTrue(actual);
    // }
    //
    // @Test
    // @Tag("Unit")
    // public void hasAccount_WithIdentifier_ShouldThrowNotImplementedException() {
    //     // Arrange
    //     EconomyService sut = new EconomyServiceImpl(null, null);
    //
    //     // Act/Assert
    //     assertThrows(
    //         NotImplementedException.class,
    //         () -> sut.hasAccount("string")
    //     );
    // }
    //
    // @Test
    // @Tag("Unit")
    // public void findOrCreateAccount_WithExistingUUID_ShouldReturnAccount() {
    //     // Arrange
    //     AccountData accountDataMock = mock(AccountData.class);
    //     UUID uuid = UUID.randomUUID();
    //     UniqueAccount account = new UniqueAccountImpl(null, null);
    //     when(accountDataMock.getAccount(uuid)).thenReturn(account);
    //
    //     EconomyService sut = new EconomyServiceImpl(null, accountDataMock);
    //
    //     // Act
    //     UniqueAccount actual = sut.findOrCreateAccount(uuid).orElse(null);
    // }
    //
    // @Test
    // @Tag("Unit")
    // public void findOrCreateAccount_WithNewUUID_ShouldCreateAndReturnNewAccount() {
    //     // Arrange
    //     AccountData accountDataMock = mock(AccountData.class);
    //     UUID uuid = UUID.randomUUID();
    //     when(accountDataMock.hasAccount(uuid)).thenReturn(false);
    //
    //     EconomyService sut = new EconomyServiceImpl(null, accountDataMock);
    //
    //     // Act
    //     UniqueAccount actual = sut.findOrCreateAccount(uuid).orElse(null);
    // }
}
