package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.dto.CreateAccountRequest;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountDataTest {
    @Test
    @Tag("Integration")
    void createAccount_WithSuccess_ShouldReturnCreatedAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var createAccountReq = new CreateAccountRequest(
            playerId,
            "USD",
            BigDecimal.ONE
        );

        var sut = new AccountData();

        //// Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.createAccount(c, createAccountReq);
            var expected = new TEAccount(
                playerId,
                "USD",
                BigDecimal.ONE
            );

            assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("balance")
                .isEqualTo(actual);
            assertEquals(0, expected.balance().compareTo(actual.balance().setScale(2, RoundingMode.DOWN)));
            return null;
        });
    }
}
