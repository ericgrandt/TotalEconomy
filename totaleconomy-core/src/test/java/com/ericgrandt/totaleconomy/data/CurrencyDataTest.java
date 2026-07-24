package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyDataTest {
    @Test
    @Tag("Integration")
    void getDefaultCurrency_WithSuccess_ShouldReturnDefaultCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new CurrencyData();

        // Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.getDefaultCurrency(c);
            var expected = new TECurrency(
                "USD",
                "Dollar",
                "Dollars",
                "$",
                2,
                BigDecimal.ZERO,
                true
            );

            assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("startingBalance")
                .isEqualTo(actual);
            assertEquals(
                0,
                expected.startingBalance().compareTo(actual.startingBalance().setScale(2, RoundingMode.DOWN))
            );
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getDefaultCurrency_WithNoDefaultCurrency_ShouldThrowMissingDefaultCurrencyException() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var util = new TransactionUtil(dataSource);

        var sut = new CurrencyData();

        // Act/Assert
        util.runInTransaction(c -> {
            assertThrows(
                MissingDefaultCurrencyException.class,
                () -> sut.getDefaultCurrency(c)
            );
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getCurrency_WithSuccess_ShouldReturnCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new CurrencyData();

        // Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.getCurrency(c, "USD").orElseThrow();
            var expected = new TECurrency(
                "USD",
                "Dollar",
                "Dollars",
                "$",
                2,
                BigDecimal.ZERO,
                true
            );

            assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("startingBalance")
                .isEqualTo(actual);
            assertEquals(
                0,
                expected.startingBalance().compareTo(actual.startingBalance().setScale(2, RoundingMode.DOWN))
            );
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getCurrency_WithNoCurrencyFoundForCode_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var util = new TransactionUtil(dataSource);

        var sut = new CurrencyData();

        // Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.getCurrency(c, "USD");
            var expected = Optional.empty();

            assertEquals(expected, actual);
            return null;
        });
    }
}
