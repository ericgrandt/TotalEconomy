package com.ericgrandt.data;

import com.ericgrandt.TestUtils;
import com.ericgrandt.domain.TECurrency;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyDataTest {
    private CurrencyData sut;

    @Mock
    private Database databaseMock;

    @Mock
    private Logger loggerMock;

    @BeforeEach
    public void init(TestInfo info) throws SQLException {
        sut = new CurrencyData(loggerMock, databaseMock);

        if (info.getTags().contains("Unit")) {
            return;
        }

        TestUtils.resetDb();
        TestUtils.seedCurrencies();

        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());
    }

    @Test
    @Tag("Unit")
    public void addAccount_WithSQLException_ShouldReturnNull() throws SQLException {
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        Currency result = sut.getDefaultCurrency();

        verify(loggerMock, times(1)).error(any(String.class));
        assertNull(result);
    }

    @Test
    @Tag("Unit")
    public void getCurrencies_WithSQLException_ShouldReturnEmptyHashSet() throws SQLException {
        when(databaseMock.getConnection()).thenThrow(SQLException.class);

        Set<Currency> result = sut.getCurrencies();

        verify(loggerMock, times(1)).error(any(String.class));
        assertTrue(result.isEmpty());
    }

    @Test
    @Tag("Integration")
    public void getDefaultCurrency_ShouldReturnTheExpectedCurrency() {
        // Act
        Currency result = sut.getDefaultCurrency();
        Currency expectedResult = new TECurrency(
            1,
            "Dollar",
            "Dollars",
            "$",
            0,
            true
        );

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Integration")
    public void getCurrencies_ShouldReturnTheExpectedCurrency() {
        // Act
        Set<Currency> result = sut.getCurrencies();
        Set<Currency> expectedResult = new HashSet<>();
        expectedResult.add(
            new TECurrency(
                1,
                "Dollar",
                "Dollars",
                "$",
                0,
                true
            )
        );
        expectedResult.add(
            new TECurrency(
                2,
                "Euro",
                "Euros",
                "E",
                0,
                false
            )
        );

        // Assert
        assertEquals(expectedResult, result);
    }
}
