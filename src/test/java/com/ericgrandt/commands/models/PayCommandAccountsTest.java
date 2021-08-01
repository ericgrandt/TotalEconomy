package com.ericgrandt.commands.models;

import com.ericgrandt.domain.TEAccount;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class PayCommandAccountsTest {
    @Test
    public void getFromAccount_ShouldReturnAccount() {
        TEAccount fromAccount = mock(TEAccount.class);
        PayCommandAccounts sut = new PayCommandAccounts(fromAccount, null);

        TEAccount result = sut.getFromAccount();

        assertEquals(fromAccount, result);
    }

    @Test
    public void getToAccount_ShouldReturnAccount() {
        TEAccount toAccount = mock(TEAccount.class);
        PayCommandAccounts sut = new PayCommandAccounts(null, toAccount);

        TEAccount result = sut.getToAccount();

        assertEquals(toAccount, result);
    }
}
