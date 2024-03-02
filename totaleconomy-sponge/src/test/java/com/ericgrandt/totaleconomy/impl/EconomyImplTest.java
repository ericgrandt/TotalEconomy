package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import java.math.BigDecimal;
import java.sql.SQLException;
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
    private CommonEconomy economyMock;

    @Mock
    private BalanceData balanceDataMock;

    private final CurrencyDto currency = new CurrencyDto(
        1,
        "Dollar",
        "Dollars",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void defaultCurrency_ShouldReturnDefaultCurrency() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, currency, economyMock, balanceDataMock);

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
        UUID uuid = UUID.randomUUID();
        when(economyMock.hasAccount(uuid)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, economyMock, balanceDataMock);

        // Act
        boolean actual = sut.hasAccount(uuid);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithAccount_ShouldReturnAccount() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(economyMock.hasAccount(uuid)).thenReturn(true);
        when(economyMock.getBalance(uuid, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, economyMock, balanceDataMock);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.of(
            new UniqueAccountImpl(
                loggerMock,
                uuid,
                Map.of(new CurrencyImpl(currency), BigDecimal.TEN),
                balanceDataMock,
                currency,
                economyMock
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithNoAccountAndSuccessfulCreate_ShouldReturnAccount() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(economyMock.hasAccount(uuid)).thenReturn(false);
        when(economyMock.createAccount(uuid, 1)).thenReturn(true);
        when(economyMock.getBalance(uuid, 1)).thenReturn(BigDecimal.TEN);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, economyMock, balanceDataMock);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.of(
            new UniqueAccountImpl(
                loggerMock,
                uuid,
                Map.of(new CurrencyImpl(currency), BigDecimal.TEN),
                balanceDataMock,
                currency,
                economyMock
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithNoAccountAndUnsuccessfulCreate_ShouldReturnEmptyOptional() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(economyMock.hasAccount(uuid)).thenReturn(false);
        when(economyMock.createAccount(uuid, 1)).thenReturn(false);

        EconomyImpl sut = new EconomyImpl(loggerMock, currency, economyMock, balanceDataMock);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.empty();

        // Assert
        assertEquals(expected, actual);
    }
}
