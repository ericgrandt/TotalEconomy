package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.dto.CreateAccountDto;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountDataTest {
    @Test
    @Tag("Integration")
    void createAccount_WithSuccess_ShouldReturnCreatedAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var createAccountDto = new CreateAccountDto(
            playerId,
            currency.code(),
            BigDecimal.ONE
        );

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.createAccount(conn, createAccountDto);
            var expected = new TEAccount(
                playerId,
                currency.code(),
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
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, null);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.getAccount(conn, UUID.fromString(account.playerId()), currency.code()).orElseThrow();
            var expected = new TEAccount(
                UUID.fromString(account.playerId()),
                currency.code(),
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
    void getAccount_WithAccountNotFound_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.getAccount(conn, UUID.randomUUID(), currency.code());
            var expected = Optional.empty();

            assertEquals(expected, actual);

            return null;
        });
    }

    @Test
    @Tag("Integration")
    void withdraw_WithSuccess_ShouldReturnAnAffectedRowCountOfOneAndWithdrawFromAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, null);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.withdraw(
                conn,
                UUID.fromString(account.playerId()),
                currency.code(),
                BigDecimal.TWO,
                true
            );

            var actualAccount = sut.getAccount(conn, UUID.fromString(account.playerId()), currency.code())
                .orElseThrow();
            var expectedBalance = BigDecimal.valueOf(8).setScale(2, RoundingMode.DOWN);

            assertTrue(actual);
            assertEquals(
                expectedBalance,
                actualAccount.balance().setScale(currency.fractionalDigits(), RoundingMode.DOWN)
            );
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void withdraw_WithEnforceMinimumBalanceOfFalse_ShouldReturnAnAffectedRowCountOfOneAndWithdrawFromAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, null);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.withdraw(
                conn,
                UUID.fromString(account.playerId()),
                currency.code(),
                BigDecimal.valueOf(15),
                false
            );

            var actualAccount = sut.getAccount(conn, UUID.fromString(account.playerId()), currency.code())
                .orElseThrow();
            var expectedBalance = BigDecimal.valueOf(-5).setScale(2, RoundingMode.DOWN);

            assertTrue(actual);
            assertEquals(
                expectedBalance,
                actualAccount.balance().setScale(currency.fractionalDigits(), RoundingMode.DOWN)
            );
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void withdraw_WithNoRowsUpdated_ShouldReturnFalse() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.withdraw(conn, UUID.randomUUID(), currency.code(), BigDecimal.TWO, true);

            assertFalse(actual);

            return null;
        });
    }

    @Test
    @Tag("Integration")
    void deposit_WithSuccess_ShouldReturnAnAffectedRowCountOfOneAndDepositIntoAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, null);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.deposit(
                conn,
                UUID.fromString(account.playerId()),
                currency.code(),
                BigDecimal.TWO
            );

            var actualAccount = sut.getAccount(conn, UUID.fromString(account.playerId()), currency.code())
                .orElseThrow();
            var expectedBalance = BigDecimal.valueOf(12).setScale(2, RoundingMode.DOWN);

            assertTrue(actual);
            assertEquals(
                expectedBalance,
                actualAccount.balance().setScale(currency.fractionalDigits(), RoundingMode.DOWN)
            );
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void deposit_WithNoRowsUpdated_ShouldReturnFalse() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedDefaultCurrency(dataSource);
        var util = new TransactionUtil(dataSource);

        var sut = new AccountData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.deposit(conn, UUID.randomUUID(), currency.code(), BigDecimal.TWO);

            assertFalse(actual);

            return null;
        });
    }
}
