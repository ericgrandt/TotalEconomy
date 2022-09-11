package com.ericgrandt.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EconomyServiceImplTest {
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
