package com.erigitic.services;

import com.erigitic.data.AccountData;
import com.erigitic.domain.Account;
import com.erigitic.domain.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    AccountService sut;

    @Mock
    AccountData accountDataMock;

    @BeforeEach
    public void init() {
        sut = new AccountService(accountDataMock);
    }

    @Test
    public void createAccount_ShouldCallCreateAccountInAccountData() {
        Account account = new Account("random-uuid", "Display Name", null);

        sut.addAccount(account);

        verify(accountDataMock, times(1)).addAccount(account);
    }

    @Test
    public void getAccount_ShouldReturnTheCorrectAccount() {
        List<Balance> balances = Arrays.asList(
            new Balance("random-uuid", 1, BigDecimal.valueOf(123)),
            new Balance("random-uuid", 2, BigDecimal.valueOf(456))
        );
        Account account = new Account(
            "random-uuid",
            "Display Name",
            balances
        );
        when(accountDataMock.getAccount("random-uuid")).thenReturn(account);

        Account result = sut.getAccount("random-uuid");

        assertEquals(account, result);
    }

    @Test
    public void getBalance_ShouldReturnTheCorrectBalance() {
        Balance balance = new Balance("random-uuid", 1, BigDecimal.valueOf(123));
        when(accountDataMock.getBalance("random-uuid", 1)).thenReturn(balance);

        Balance result = sut.getBalance("random-uuid", 1);

        assertEquals(balance, result);
    }

    @Test
    public void getBalances_ShouldReturnAListOfBalances() {
        List<Balance> balances = Arrays.asList(
            new Balance("random-uuid", 1, BigDecimal.valueOf(123)),
            new Balance("random-uuid", 2, BigDecimal.valueOf(456))
        );
        when(accountDataMock.getBalances("random-uuid")).thenReturn(balances);

        List<Balance> result = sut.getBalances("random-uuid");

        assertEquals(balances, result);
    }

    @Test
    public void setBalance_WithNumberGreaterThanOrEqualToZero_ShouldSetBalance() {
        Balance updatedBalance = new Balance("random-uuid", 1, BigDecimal.valueOf(0));

        when(accountDataMock.setBalance(any(Balance.class))).thenReturn(updatedBalance);

        Balance result = sut.setBalance(updatedBalance);

        assertEquals(updatedBalance, result);
    }

    @Test
    public void setBalance_WithNumberLessThanZero_ShouldThrowAnIllegalArgumentException() {
        Balance updatedBalance = new Balance("random-uuid", 1, BigDecimal.valueOf(-1));

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.setBalance(updatedBalance),
            "Balance must be greater than or equal to zero"
        );
    }

    @Test
    public void transfer_WithValidData_ShouldUpdateBothBalances() {

    }

    @Test
    public void transfer_WithNotEnoughMoneyInFromBalance_ShouldThrowIllegalArgumentException() {

    }

    @Test
    public void transfer_WithNonMatchingCurrencyIds_ShouldThrowIllegalArgumentException() {

    }
}
