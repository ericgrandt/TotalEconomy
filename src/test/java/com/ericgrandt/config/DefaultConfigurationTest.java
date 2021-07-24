package com.ericgrandt.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class DefaultConfigurationTest {
    private DefaultConfiguration sut;

    @BeforeEach
    public void init() {
        sut = new DefaultConfiguration();
    }

    @Test
    public void getConnectionString_WithValidConfig_ShouldReturnCorrectString() {
        String result = sut.getConnectionString();

        assertEquals(result, "jdbc:mysql://localhost:3306/totaleconomy?user=user&password=password");
    }
}
