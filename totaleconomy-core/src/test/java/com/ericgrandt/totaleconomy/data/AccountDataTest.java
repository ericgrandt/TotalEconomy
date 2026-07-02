package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.dto.CreateAccountDto;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountDataTest {
    @Test
    @Tag("Integration")
    void createAccount_WithSuccess_ShouldReturnCreatedAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var createAccountDto = new CreateAccountDto(
            playerId,
            "USD",
            BigDecimal.ONE
        );

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.createAccount(c, createAccountDto);
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

    @Test
    @Tag("Integration")
    void getAccount_WithSuccess_ShouldReturnAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, null);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(c -> {
            var actual = sut.getAccount(c, UUID.fromString(account.playerId()), account.currencyCode());
            var expected = new TEAccount(
                UUID.fromString(account.playerId()),
                "USD",
                BigDecimal.TEN
            );

            assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("balance")
                .isEqualTo(actual);
            assertEquals(0, expected.balance().compareTo(actual.balance().setScale(2, RoundingMode.DOWN)));
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getAccount_WithAccountNotFound_ShouldThrowAccountNotFoundException() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(c -> {
            assertThrows(AccountNotFoundException.class, () -> sut.getAccount(c, UUID.randomUUID(), "USD"));

            return null;
        });
    }
}
