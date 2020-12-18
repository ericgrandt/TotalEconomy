package com.erigitic.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.erigitic.TotalEconomy;
import java.io.File;
import ninja.leaping.configurate.ConfigurationNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class DefaultConfigurationTest {
    private DefaultConfiguration sut;

    @Mock
    private TotalEconomy pluginMock;

    @BeforeEach
    public void init() {
        sut = new DefaultConfiguration(pluginMock);
        when(pluginMock.getConfigDir()).thenReturn(new File("src/test/resources/assets/totaleconomy"));
    }

    @Test
    public void loadConfiguration_WithValidFile_ShouldReturnAConfigurationNode() {
        ConfigurationNode result = sut.loadConfiguration("totaleconomy.conf");

        assertNotNull(result);
    }

    @Test
    public void getConnectionString_WithValidConfig_ShouldReturnCorrectString() {
        sut.loadConfiguration("totaleconomy.conf");

        String result = sut.getConnectionString();

        assertEquals(result, "jdbc:mysql://test:1234/database?user=username&password=password");
    }
}
