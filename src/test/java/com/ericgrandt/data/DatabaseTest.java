package com.ericgrandt.data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.TotalEconomy;
import com.ericgrandt.config.DefaultConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.h2.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;

@ExtendWith(MockitoExtension.class)
public class DatabaseTest {
    private Database sut;

    @Mock
    private TotalEconomy pluginMock;

    @Mock
    private PluginContainer pluginContainerMock;

    @Mock
    private Logger loggerMock;

    @Mock
    private ValueReference<DefaultConfiguration, CommentedConfigurationNode> valueReference;

    @BeforeEach
    public void init() {
        DefaultConfiguration config = mock(DefaultConfiguration.class);
        when(pluginMock.getDefaultConfiguration()).thenReturn(valueReference);
        when(pluginMock.getDefaultConfiguration().get()).thenReturn(config);
        when(pluginContainerMock.instance()).thenReturn(pluginMock);

        sut = new Database(loggerMock, pluginContainerMock);
    }

    @Test
    @Tag("Unit")
    public void setup_WithUnsupportedDbProvider_ShouldLogError() {
        DefaultConfiguration config = Objects.requireNonNull(pluginMock.getDefaultConfiguration().get());
        when(config.getConnectionString()).thenReturn("jdbc:unsupporteddb://localhost:3306/totaleconomy");

        sut.setup();

        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Unit")
    public void setup_WithSQLException_ShouldLogError() {
        DefaultConfiguration config = Objects.requireNonNull(pluginMock.getDefaultConfiguration().get());
        when(config.getConnectionString()).thenReturn("jdbc:h2:error:totaleconomy");

        sut.setup();

        verify(loggerMock, times(1)).error(any(String.class), any(String.class));
    }

    @Test
    @Tag("Unit")
    public void setup_WithIOException_ShouldLogError() throws IOException {
        DefaultConfiguration config = Objects.requireNonNull(pluginMock.getDefaultConfiguration().get());
        when(config.getConnectionString()).thenReturn("jdbc:h2:mem:totaleconomy");
        when(pluginContainerMock.openResource(any(URI.class))).thenReturn(Optional.empty());

        sut.setup();

        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Integration")
    public void setup_WithSupportedDbProvider_ShouldNotLogError() throws IOException {
        DefaultConfiguration config = Objects.requireNonNull(pluginMock.getDefaultConfiguration().get());
        InputStream inputStreamMock = IOUtils.getInputStream(";");
        when(config.getConnectionString()).thenReturn("jdbc:h2:mem:totaleconomy");
        when(pluginContainerMock.openResource(any(URI.class))).thenReturn(Optional.of(inputStreamMock));

        sut.setup();

        verify(loggerMock, times(2)).info(any(String.class));
    }
}
