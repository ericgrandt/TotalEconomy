package com.erigitic.services;

import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import com.erigitic.domain.TEAccount;
import com.erigitic.domain.TECurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class TEEconomyServiceTest {
    TEEconomyService sut;

    @Mock
    AccountData accountDataMock;

    @Mock
    CurrencyData currencyDataMock;

    @BeforeEach
    public void init() {
        sut = new TEEconomyService(accountDataMock, currencyDataMock);
    }

    @Test
    public void getDefaultCurrency_ShouldReturnCorrectCurrency() {
        TECurrency currency = new TECurrency(
            1,
            "Test",
            "Tests",
            "$",
            true
        );
        when(currencyDataMock.getDefaultCurrency()).thenReturn(currency);

        Currency result = sut.getDefaultCurrency();

        assertEquals(currency, result);
    }

    @Test
    public void getCurrencies_ShouldReturnCorrectSetOfCurrencies() {
        Set<Currency> currencies = new HashSet<>();

        currencies.add(
            new TECurrency(
                1,
            "Test",
            "Tests",
            "$",
            true
            )
        );
        currencies.add(
            new TECurrency(
                2,
                "Test2",
                "Tests2",
                "A",
                false
            )
        );
        when(currencyDataMock.getCurrencies()).thenReturn(currencies);

        Set<Currency> result = sut.getCurrencies();

        assertEquals(currencies, result);
    }

    @Test
    public void hasAccountUuid_WithValidUuid_ShouldReturnTrue() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(account);

        boolean result = sut.hasAccount(uuid);

        assertTrue(result);
    }

    @Test
    public void hasAccountUuid_WithInvalidUuid_ShouldReturnFalse() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(null);

        boolean result = sut.hasAccount(uuid);

        assertFalse(result);
    }

    @Test
    public void getOrCreateAccountUuid_WithExistingUuid_ShouldReturnExistingAccount() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(account);

        Optional<UniqueAccount> result = sut.getOrCreateAccount(uuid);

        assertEquals(account, result.orElse(null));
    }

    @Test
    public void getOrCreateAccountUuid_WithExistingUuid_ShouldNotAddAccount() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(account);

        sut.getOrCreateAccount(uuid);

        verify(accountDataMock, times(0)).addAccount(any(TEAccount.class));
    }

    @Test
    public void getOrCreateAccountUuid_WithNonExistingUuid_ShouldAddAccountAndReturnIt() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "",
            new HashMap<>()
        );

        when(accountDataMock.getAccount(uuid)).thenReturn(null);

        Optional<UniqueAccount> result = sut.getOrCreateAccount(uuid);

        verify(accountDataMock, times(1)).addAccount(any(TEAccount.class));
        assertEquals(account, result.orElse(null));
    }
}
