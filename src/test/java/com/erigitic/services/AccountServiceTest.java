package com.erigitic.services;

import com.erigitic.data.AccountData;
import com.erigitic.domain.Balance;
import com.erigitic.domain.TEAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    AccountService sut;

    @Mock
    AccountData accountDataMock;

    @Mock
    Currency currencyMock;

    @BeforeEach
    public void init() {
        sut = new AccountService(accountDataMock);
    }

    @Test
    public void addAccount_ShouldCallCreateAccountInAccountData() {
        TEAccount account = new TEAccount(UUID.randomUUID(), "Display Name", null);

        sut.addAccount(account);

        verify(accountDataMock, times(1)).addAccount(account);
    }

    @Test
    public void getAccount_ShouldReturnTheCorrectAccount() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(
            uuid,
            "Display Name",
            balances
        );
        when(accountDataMock.getAccount(uuid)).thenReturn(account);

        Account result = sut.getAccount(uuid);

        assertEquals(account, result);
    }

    @Test
    public void getBalance_ShouldReturnTheCorrectBalance() {
        UUID uuid = UUID.randomUUID();
        Balance balance = new Balance(uuid, 1, BigDecimal.valueOf(123));
        when(accountDataMock.getBalance(uuid, 1)).thenReturn(balance);

        Balance result = sut.getBalance(uuid, 1);

        assertEquals(balance, result);
    }

    @Test
    public void getBalances_ShouldReturnAListOfBalances() {
        UUID uuid = UUID.randomUUID();
        List<Balance> balances = Arrays.asList(
            new Balance(uuid, 1, BigDecimal.valueOf(123)),
            new Balance(uuid, 2, BigDecimal.valueOf(456))
        );
        when(accountDataMock.getBalances(uuid)).thenReturn(balances);

        List<Balance> result = sut.getBalances(uuid);

        assertEquals(balances, result);
    }

    @Test
    public void setBalance_WithNumberGreaterThanOrEqualToZero_ShouldSetBalance() {
        Balance updatedBalance = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(0));

        when(accountDataMock.setBalance(updatedBalance)).thenReturn(updatedBalance);

        Balance result = sut.setBalance(updatedBalance);

        assertEquals(updatedBalance, result);
    }

    @Test
    public void setBalance_WithNumberLessThanZero_ShouldThrowAnIllegalArgumentException() {
        Balance updatedBalance = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(-1));

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.setBalance(updatedBalance),
            "Balance must be greater than or equal to zero"
        );
    }

    @Test
    public void transfer_WithValidData_ShouldUpdateBothBalances() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(100));
        Balance to = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(50));

        when(accountDataMock.setTransferBalances(from, to)).thenReturn(true);

        boolean result = sut.transfer(from, to, BigDecimal.valueOf(40));

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(60), from.getBalance());
        assertEquals(BigDecimal.valueOf(90), to.getBalance());
    }

    @Test
    public void transfer_WithNotEnoughMoneyInFromBalance_ShouldThrowIllegalArgumentException() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(24));
        Balance to = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(50));

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.transfer(from, to, BigDecimal.valueOf(25)),
            "Transfer amount is more than the user has"
        );
    }

    @Test
    public void transfer_WithNonMatchingCurrencyIds_ShouldThrowIllegalArgumentException() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(100));
        Balance to = new Balance(UUID.randomUUID(), 2, BigDecimal.valueOf(50));

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.transfer(from, to, BigDecimal.valueOf(25)),
            "Currency ids do not match"
        );
    }
}
