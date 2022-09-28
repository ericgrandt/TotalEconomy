package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;

public class AccountDeletionResultTypeImplTest {
    @Test
    @Tag("Unit")
    public void isSuccess_WithIsSuccessOfTrue_ShouldReturnTrue() {
        // Arrange
        AccountDeletionResultType sut = new AccountDeletionResultTypeImpl(true);

        // Act
        boolean actual = sut.isSuccess();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isSuccess_WithIsSuccessOfFalse_ShouldReturnFalse() {
        // Arrange
        AccountDeletionResultType sut = new AccountDeletionResultTypeImpl(false);

        // Act
        boolean actual = sut.isSuccess();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        AccountDeletionResultType sut = new AccountDeletionResultTypeImpl(false);

        // Act
        boolean actual = sut.equals(sut);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentObjectTypes_ShouldReturnFalse() {
        // Arrange
        AccountDeletionResultType sut = new AccountDeletionResultTypeImpl(false);
        Object obj = new Object();

        // Act
        boolean actual = sut.equals(obj);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameIsSuccess_ShouldReturnTrue() {
        // Arrange
        AccountDeletionResultType sut = new AccountDeletionResultTypeImpl(true);
        AccountDeletionResultType other = new AccountDeletionResultTypeImpl(true);

        // Act
        boolean actual = sut.equals(other);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentIsSuccess_ShouldReturnFalse() {
        // Arrange
        AccountDeletionResultType sut = new AccountDeletionResultTypeImpl(true);
        AccountDeletionResultType other = new AccountDeletionResultTypeImpl(false);

        // Act
        boolean actual = sut.equals(other);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_ShouldReturnCorrectHashCode() {
        // Arrange
        AccountDeletionResultType sut1 = new AccountDeletionResultTypeImpl(true);
        AccountDeletionResultType sut2 = new AccountDeletionResultTypeImpl(true);

        // Act
        int actual1 = sut1.hashCode();
        int actual2 = sut2.hashCode();

        // Assert
        assertEquals(actual1, actual2);
    }
}
