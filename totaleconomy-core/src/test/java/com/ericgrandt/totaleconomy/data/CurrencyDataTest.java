package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.exception.EntityNotFoundException;
import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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

        //// Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.getDefaultCurrency(c);
            var expected = new TECurrency(
                "USD",
                "Dollar",
                "Dollars",
                "$",
                2,
                true
            );

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getDefaultCurrency_WithNoDefaultCurrency_ShouldThrowEntityNotFoundException() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var util = new TransactionUtil(dataSource);

        var sut = new CurrencyData();

        //// Act/Assert
        util.runInTransaction(c -> {
            assertThrows(
                EntityNotFoundException.class,
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

        //// Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.getCurrency(c, "USD");
            var expected = new TECurrency(
                "USD",
                "Dollar",
                "Dollars",
                "$",
                2,
                true
            );

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getCurrency_WithNoCurrencyFoundForCode_ShouldThrowEntityNotFoundException() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new CurrencyData();

        //// Act/Assert
        util.runInTransaction(c -> {
            assertThrows(
                EntityNotFoundException.class,
                () -> sut.getCurrency(c, "COIN")
            );
            return null;
        });
    }
}
