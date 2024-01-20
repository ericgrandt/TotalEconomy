package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@ExtendWith(MockitoExtension.class)
public class EconomyImplTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private AccountData accountDataMock;

    @Mock
    private BalanceData balanceDataMock;

    private final CurrencyDto currency = new CurrencyDto(1, "Dollar", "Dollars", "$", 2, true);

    @Test
    @Tag("Unit")
    public void defaultCurrency_ShouldReturnDefaultCurrency() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        Currency actual = sut.defaultCurrency();
        Currency expected = new CurrencyImpl(currency);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        AccountDto accountDto = new AccountDto(
            UUID.randomUUID().toString(),
            Timestamp.from(Instant.now())
        );
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(accountDto);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(UUID.fromString(accountDto.id()));

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithNoAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(UUID.randomUUID());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithException_ShouldReturnFalse() throws SQLException {
        // Arrange
        when(accountDataMock.getAccount(any(UUID.class))).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(UUID.randomUUID());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithAccount_ShouldReturnAccount() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AccountDto accountDto = new AccountDto(
            uuid.toString(),
            Timestamp.from(Instant.now())
        );
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(accountDto);
        when(balanceDataMock.getBalance(any(UUID.class), any(Integer.class))).thenReturn(
            BigDecimal.TEN
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.of(
            new UniqueAccountImpl(
                loggerMock,
                uuid,
                Map.of(new CurrencyImpl(currency), BigDecimal.TEN),
                balanceDataMock,
                currency
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithNoAccount_ShouldCreateAndReturnAccount() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(null);
        when(accountDataMock.createAccount(any(UUID.class), any(Integer.class))).thenReturn(true);
        when(balanceDataMock.getBalance(any(UUID.class), any(Integer.class))).thenReturn(
            BigDecimal.TEN
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.of(
            new UniqueAccountImpl(
                loggerMock,
                uuid,
                Map.of(new CurrencyImpl(currency), BigDecimal.TEN),
                balanceDataMock,
                currency
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithException_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(balanceDataMock.getBalance(any(UUID.class), any(Integer.class))).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, accountDataMock, balanceDataMock);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.empty();

        // Assert
        assertEquals(expected, actual);
    }
}
