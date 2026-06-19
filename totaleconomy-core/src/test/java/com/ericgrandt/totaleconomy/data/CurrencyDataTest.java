package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CurrencyDataTest {
    @Test
    @Tag("Integration")
    void getDefaultCurrency_WithSuccess_ShouldReturnDefaultCurrency() throws SQLException {
        // Arrange
        TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency();
        var util = new TransactionUtil(TestUtils.d);

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
        //transaction {
        //    val actual = sut.getDefaultCurrency()
        //    val expectedTECurrency =
        //            TECurrency(
        //                    "USD",
        //                    "Dollar",
        //                    "Dollars",
        //                    "$",
        //                    2,
        //                    true,
        //                    )
        //    val expected = Ok(expectedTECurrency)

        //    assertEquals(expected, actual)
        //}
    }

}
