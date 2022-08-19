package com.ericgrandt.data.dto;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AccountDtoTest {
    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AccountDto accountDto1 = new AccountDto(
            uuid.toString(),
            null
        );
        AccountDto accountDto2 = new AccountDto(
            uuid.toString(),
            null
        );

        // Act
        boolean actual = accountDto1.equals(accountDto2);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        AccountDto accountDto1 = new AccountDto(
            UUID.randomUUID().toString(),
            null
        );

        // Act
        boolean actual = accountDto1.equals(accountDto1);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        AccountDto accountDto1 = new AccountDto(
            UUID.randomUUID().toString(),
            null
        );

        // Act
        boolean actual = accountDto1.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithWrongClass_ShouldReturnFalse() {
        // Arrange
        AccountDto accountDto1 = new AccountDto(
            UUID.randomUUID().toString(),
            null
        );
        Object accountDto2 = new Object();

        // Act
        boolean actual = accountDto1.equals(accountDto2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        AccountDto accountDto1 = new AccountDto(
            UUID.randomUUID().toString(),
            null
        );
        AccountDto accountDto2 = new AccountDto(
            UUID.randomUUID().toString(),
            null
        );

        // Act
        boolean actual = accountDto1.equals(accountDto2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentCreated_ShouldReturnFalse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AccountDto accountDto1 = new AccountDto(
            uuid.toString(),
            Timestamp.valueOf("2022-01-01 00:00:00")
        );
        AccountDto accountDto2 = new AccountDto(
            uuid.toString(),
            null
        );

        // Act
        boolean actual = accountDto1.equals(accountDto2);

        // Assert
        assertFalse(actual);
    }
}