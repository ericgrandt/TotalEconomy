package com.ericgrandt.data;

import com.ericgrandt.TestUtils;
import com.ericgrandt.TotalEconomy;
import com.ericgrandt.config.DefaultConfiguration;
import org.h2.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.configurate.reference.ValueReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatabaseTest {
    private Database sut;

    @Mock
    private TotalEconomy pluginMock;

    @Mock
    private Logger loggerMock;

    @Mock
    Asset assetMock;

    @BeforeEach
    public void init(TestInfo info) throws SQLException {
        sut = new Database(loggerMock, pluginMock);

        if (info.getTags().contains("Unit")) {
            return;
        }

        // when(DriverManager.getConnection("jdbc:h2:mem:totaleconomy")).thenReturn(TestUtils.getConnection());
    }

    @Test
    @Tag("Unit")
    public void setup_WithUnsupportedDbProvider_ShouldLogError() {
        sut = new Database(loggerMock, pluginMock);
        ValueReference valueReference = mock(ValueReference.class);
        DefaultConfiguration config = mock(DefaultConfiguration.class);
        when(pluginMock.getDefaultConfiguration()).thenReturn(valueReference);
        when(pluginMock.getDefaultConfiguration().get()).thenReturn(config);
        when(pluginMock.getDefaultConfiguration().get().getConnectionString())
            .thenReturn("jdbc:unsupporteddb://localhost:3306/totaleconomy");

        sut.setup();
        verify(loggerMock, times(1)).error(any(String.class));
    }

    @Test
    @Tag("Integration")
    public void setup_WithSupportedDbProvider_ShouldNotLogError() throws IOException {
        sut = new Database(loggerMock, pluginMock);
        ValueReference valueReference = mock(ValueReference.class);
        DefaultConfiguration config = mock(DefaultConfiguration.class);
        when(pluginMock.getDefaultConfiguration()).thenReturn(valueReference);
        when(pluginMock.getDefaultConfiguration().get()).thenReturn(config);
        when(pluginMock.getDefaultConfiguration().get().getConnectionString())
            .thenReturn("jdbc:h2:mem:totaleconomy");

        URL urlMock = mock(URL.class);
        InputStream inputStreamMock = IOUtils.getInputStream(";");;
        when(pluginMock.getMysqlSchema()).thenReturn(assetMock);
        when(assetMock.url()).thenReturn(urlMock);
        when(urlMock.openStream()).thenReturn(inputStreamMock);

        sut.setup();
        verify(loggerMock, times(2)).info(any(String.class));
    }
}
