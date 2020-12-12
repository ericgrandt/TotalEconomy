package com.erigitic.data;

import com.erigitic.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("Integration")
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private AccountService sut;

    @Mock
    private Database databaseMock;

    @BeforeEach
    public void init() throws SQLException {
        TestUtils.resetDb();
        TestUtils.seedDb();

        sut = new AccountService(databaseMock);
        when(databaseMock.getConnection()).thenReturn(TestUtils.createTestConnection());
    }

    @Test
    public void createAccount_WithValidData_ShouldInsertASingleAccountAndABalanceForBothCurrencies() throws SQLException {
        // Act
        sut.createAccount("ba64d376-8580-43b3-a3ee-2d6321114042");

        // Assert
        try (Connection conn = TestUtils.createTestConnection()) {
            assert conn != null;

            Statement userCountStmt = conn.createStatement();
            ResultSet userCount = userCountStmt.executeQuery(
                "SELECT COUNT(*) as rowcount FROM te_user\n" +
                    "WHERE id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            userCount.next();

            Statement balanceCountStmt = conn.createStatement();
            ResultSet balanceCount = balanceCountStmt.executeQuery(
                "SELECT COUNT(*) as rowcount FROM te_balance\n" +
                "WHERE user_id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            );
            balanceCount.next();

            // Statement balancesStmt = conn.createStatement();
            // ResultSet balances = balancesStmt.executeQuery(
            //     "SELECT * FROM te_balance\n" +
            //         "WHERE user_id='ba64d376-8580-43b3-a3ee-2d6321114042'"
            // );

            int userCountResult = userCount.getInt("rowcount");
            int expectedUserCount = 1;
            int balanceCountResult = balanceCount.getInt("rowcount");
            int expectedBalanceCount = 2;

            assertEquals(userCountResult, expectedUserCount);
            assertEquals(balanceCountResult, expectedBalanceCount);
        }
    }
}
