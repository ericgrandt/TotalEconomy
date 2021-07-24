package com.ericgrandt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TEAccount;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

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

        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> sut.setBalance(updatedBalance)
        );

        String result = e.getMessage();
        String expectedResult = "Balance must be greater than or equal to zero";

        assertEquals(expectedResult, result);
    }

    @Test
    public void setTransferBalances_WithValidData_ShouldUpdateBothBalances() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(100));
        Balance to = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(50));

        when(accountDataMock.setTransferBalances(from, to)).thenReturn(true);

        boolean result = sut.setTransferBalances(from, to);

        assertTrue(result);
    }

    @Test
    public void setTransferBalances_WithNegativeFromBalance_ShouldThrowIllegalArgumentException() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(-1));
        Balance to = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(50));

        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> sut.setTransferBalances(from, to)
        );

        String result = e.getMessage();
        String expectedResult = "From balance cannot be negative";

        assertEquals(expectedResult, result);
    }

    @Test
    public void setTransferBalances_WithNegativeToBalance_ShouldThrowIllegalArgumentException() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(100));
        Balance to = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(-1));

        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> sut.setTransferBalances(from, to)
        );

        String result = e.getMessage();
        String expectedResult = "To balance cannot be negative";

        assertEquals(expectedResult, result);
    }

    @Test
    public void setTransferBalances_WithNonMatchingCurrencyIds_ShouldThrowIllegalArgumentException() {
        Balance from = new Balance(UUID.randomUUID(), 1, BigDecimal.valueOf(100));
        Balance to = new Balance(UUID.randomUUID(), 2, BigDecimal.valueOf(50));

        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> sut.setTransferBalances(from, to)
        );

        String result = e.getMessage();
        String expectedResult = "Currency ids do not match";

        assertEquals(expectedResult, result);
    }
}
