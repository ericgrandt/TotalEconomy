package com.erigitic.config;

import com.erigitic.TotalEconomy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void loadConfiguration_WithValidFile_ShouldNotBeNull() {
        assertNotNull(sut.loadConfiguration("totaleconomy.conf"));
    }

    @Test
    public void getConnectionString_WithValidConfig_ShouldReturnCorrectString() {
        sut.loadConfiguration("totaleconomy.conf");

        String result = sut.getConnectionString();

        assertEquals(result, "jdbc:mysql://test:1234/database?user=username&password=password");
    }
}
