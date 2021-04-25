package com.erigitic.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.erigitic.TotalEconomy;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.ConfigurationNode;

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
