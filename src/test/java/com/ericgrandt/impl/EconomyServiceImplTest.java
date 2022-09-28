package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.VirtualAccountData;
import com.ericgrandt.data.dto.AccountDto;
import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;

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
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenReturn(account);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        boolean actual = sut.hasAccount(uuid);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithNoAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenReturn(null);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        boolean actual = sut.hasAccount(uuid);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCaughtSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        boolean actual = sut.hasAccount(uuid);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCaughtSqlException_ShouldLogError() throws SQLException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(uuid)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.hasAccount(uuid);

        // Assert
        verify(loggerMock, times(1)).error(
            eq(String.format("Error calling hasAccount (playerUUID: %s)", uuid)),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithVirtualAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        VirtualAccountDto virtualAccount = mock(VirtualAccountDto.class);
        String identifier = "random";
        when(virtualAccountDataMock.getVirtualAccount(identifier)).thenReturn(virtualAccount);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        boolean actual = sut.hasAccount(identifier);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithNoVirtualAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        String identifier = "random";
        when(virtualAccountDataMock.getVirtualAccount(identifier)).thenReturn(null);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        boolean actual = sut.hasAccount(identifier);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCaughtSqlExceptionGettingVirtualAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        String identifier = "random";
        when(virtualAccountDataMock.getVirtualAccount(identifier)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        boolean actual = sut.hasAccount(identifier);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithCaughtSqlExceptionGettingVirtualAccount_ShouldLogError() throws SQLException {
        // Arrange
        VirtualAccountData virtualAccountDataMock = mock(VirtualAccountData.class);
        String identifier = "random";
        when(virtualAccountDataMock.getVirtualAccount(identifier)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, null, virtualAccountDataMock);

        // Act
        sut.hasAccount(identifier);

        // Assert
        verify(loggerMock, times(1)).error(
            eq(String.format("Error calling hasAccount (identifier: %s)", identifier)),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithFoundAccount_ShouldReturnUniqueAccount() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        AccountDto account = new AccountDto(
            uuid.toString(),
            null
        );
        when(accountDataMock.getAccount(uuid)).thenReturn(account);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.of(
            new UniqueAccountImpl(
                uuid,
                new HashMap<>()
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithSqlExceptionFromFindingAccount_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.empty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithSqlExceptionFromFindingAccount_ShouldLogError() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.findOrCreateAccount(uuid);

        // Assert
        verify(loggerMock, times(1)).error(
            eq(String.format("Error calling getAccount (uuid: %s)", uuid.toString())),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithNullResponseFromFindingAccount_ShouldCreateAndReturnAnAccount() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        AccountDto createdAccountDto = new AccountDto(uuid.toString(), null);
        when(accountDataMock.getAccount(uuid)).thenReturn(null).thenReturn(createdAccountDto);
        when(accountDataMock.createAccount(uuid)).thenReturn(1);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.of(
            new UniqueAccountImpl(
                uuid,
                new HashMap<>()
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithSqlExceptionFromCreatingAccount_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenReturn(null);
        when(accountDataMock.createAccount(uuid)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Optional<UniqueAccount> actual = sut.findOrCreateAccount(uuid);
        Optional<UniqueAccount> expected = Optional.empty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void findOrCreateAccount_WithSqlExceptionFromCreatingAccount_ShouldLogError() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        UUID uuid = UUID.randomUUID();
        when(accountDataMock.getAccount(uuid)).thenReturn(null);
        when(accountDataMock.createAccount(uuid)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.findOrCreateAccount(uuid);

        // Assert
        verify(loggerMock, times(1)).error(
            eq(String.format("Error calling createAndGetAccount (uuid: %s)", uuid.toString())),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void streamUniqueAccounts_WithSuccess_ShouldReturnStreamOfAccounts() throws SQLException {
        // Arrange
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();
        List<AccountDto> accounts = Arrays.asList(
            new AccountDto(accountId1.toString(), null),
            new AccountDto(accountId2.toString(), null)
        );
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccounts()).thenReturn(accounts);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Stream<UniqueAccount> actual = sut.streamUniqueAccounts();

        List<UniqueAccount> expectedAccounts = Arrays.asList(
            new UniqueAccountImpl(accountId1, new HashMap<>()),
            new UniqueAccountImpl(accountId2, new HashMap<>())
        );
        Stream<UniqueAccount> expected = expectedAccounts.stream();

        // Assert
        Iterator actualIter = actual.iterator();
        Iterator expectedIter = expected.iterator();
        while (actualIter.hasNext() && expectedIter.hasNext()) {
            assertEquals(actualIter.next(), expectedIter.next());
        }
    }

    @Test
    @Tag("Unit")
    public void streamUniqueAccounts_WithSqlException_ShouldReturnEmptyStream() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccounts()).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Stream<UniqueAccount> actual = sut.streamUniqueAccounts();

        // Assert
        assertFalse(actual.findAny().isPresent());
    }

    @Test
    @Tag("Unit")
    public void streamUniqueAccounts_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccounts()).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.streamUniqueAccounts();

        // Assert
        verify(loggerMock, times(1)).error(
            eq("Error calling streamUniqueAccounts"),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void uniqueAccounts_WithSuccess_ShouldReturnListOfAccounts() throws SQLException {
        // Arrange
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();
        List<AccountDto> accounts = Arrays.asList(
            new AccountDto(accountId1.toString(), null),
            new AccountDto(accountId2.toString(), null)
        );
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccounts()).thenReturn(accounts);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Collection<UniqueAccount> actual = sut.uniqueAccounts();
        Collection<UniqueAccount> expected = Arrays.asList(
            new UniqueAccountImpl(accountId1, new HashMap<>()),
            new UniqueAccountImpl(accountId2, new HashMap<>())
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void uniqueAccounts_WithSqlException_ShouldReturnEmptyCollection() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccounts()).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        Collection<UniqueAccount> actual = sut.uniqueAccounts();

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void uniqueAccounts_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccounts()).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.uniqueAccounts();

        // Assert
        verify(loggerMock, times(1)).error(
            eq("Error calling streamUniqueAccounts"),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void deleteAccount_WithTrueResponse_ShouldReturnSuccessfulAccountDeletionResultType() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.deleteAccount(accountId)).thenReturn(true);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        AccountDeletionResultType actual = sut.deleteAccount(accountId);
        AccountDeletionResultType expected = new AccountDeletionResultTypeImpl(true);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deleteAccount_WithFalseResponse_ShouldReturnSuccessfulAccountDeletionResultType() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.deleteAccount(accountId)).thenReturn(false);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        AccountDeletionResultType actual = sut.deleteAccount(accountId);
        AccountDeletionResultType expected = new AccountDeletionResultTypeImpl(true);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deleteAccount_WithSqlException_ShouldReturnUnsuccessfulAccountDeletionResultType() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.deleteAccount(accountId)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        AccountDeletionResultType actual = sut.deleteAccount(accountId);
        AccountDeletionResultType expected = new AccountDeletionResultTypeImpl(false);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deleteAccount_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.deleteAccount(accountId)).thenThrow(SQLException.class);

        EconomyServiceImpl sut = new EconomyServiceImpl(loggerMock, accountDataMock, null);

        // Act
        sut.deleteAccount(accountId);

        // Assert
        verify(loggerMock, times(1)).error(
            eq(String.format("Error calling deleteAccount (uuid: %s)", accountId)),
            any(SQLException.class)
        );
    }
}
