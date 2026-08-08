package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.common.testutils.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class PlayerActiveJobDataTest {
    @Test
    @Tag("Integration")
    void createAccount_WithSuccess_ShouldReturnCreatedAccount() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var util = new TransactionUtil(dataSource);

        //var playerId = UUID.randomUUID();
        //var createAccountDto = new CreateAccountDto(
        //    playerId,
        //    currency.code(),
        //    BigDecimal.ONE
        //);

        //var sut = new AccountData();

        //// Act/Assert
        //util.runInTransaction(conn -> {
        //    var actual = sut.createAccount(conn, createAccountDto);
        //    var expected = new TEAccount(
        //        playerId,
        //        currency.code(),
        //        BigDecimal.ONE
        //    );

        //    assertThat(expected)
        //        .usingRecursiveComparison()
        //        .ignoringFields("balance")
        //        .isEqualTo(actual);
        //    assertEquals(0, expected.balance().compareTo(actual.balance().setScale(2, RoundingMode.DOWN)));
        //    return null;
        //});
    }
}
