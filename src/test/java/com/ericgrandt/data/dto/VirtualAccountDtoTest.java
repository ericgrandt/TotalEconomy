package com.ericgrandt.data.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VirtualAccountDtoTest {
    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String identifier = "virtualAccount";
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            uuid.toString(),
            identifier,
            null
        );
        VirtualAccountDto virtualAccountDto2 = new VirtualAccountDto(
            uuid.toString(),
            identifier,
            null
        );

        // Act
        boolean actual = virtualAccountDto1.equals(virtualAccountDto2);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            UUID.randomUUID().toString(),
            "virtualAccount",
            null
        );

        // Act
        boolean actual = virtualAccountDto1.equals(virtualAccountDto1);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            UUID.randomUUID().toString(),
            "virtualAccount",
            null
        );

        // Act
        boolean actual = virtualAccountDto1.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithWrongClass_ShouldReturnFalse() {
        // Arrange
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            UUID.randomUUID().toString(),
            "virtualAccount",
            null
        );
        Object virtualAccountDto2 = new Object();

        // Act
        boolean actual = virtualAccountDto1.equals(virtualAccountDto2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        String identifier = "virtualAccount";
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            UUID.randomUUID().toString(),
            identifier,
            null
        );
        VirtualAccountDto virtualAccountDto2 = new VirtualAccountDto(
            UUID.randomUUID().toString(),
            identifier,
            null
        );

        // Act
        boolean actual = virtualAccountDto1.equals(virtualAccountDto2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentIdentifier_ShouldReturnFalse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            uuid.toString(),
            "virtualAccount1",
            null
        );
        VirtualAccountDto virtualAccountDto2 = new VirtualAccountDto(
            uuid.toString(),
            "virtualAccount2",
            null
        );

        // Act
        boolean actual = virtualAccountDto1.equals(virtualAccountDto2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentCreated_ShouldReturnFalse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String identifier = "virtualAccount";
        VirtualAccountDto virtualAccountDto1 = new VirtualAccountDto(
            uuid.toString(),
            identifier,
            Timestamp.valueOf("2022-01-01 00:00:00")
        );
        VirtualAccountDto virtualAccountDto2 = new VirtualAccountDto(
            uuid.toString(),
            identifier,
            null
        );

        // Act
        boolean actual = virtualAccountDto1.equals(virtualAccountDto2);

        // Assert
        assertFalse(actual);
    }
}
