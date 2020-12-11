package com.erigitic.data;

import com.erigitic.TestUtils;
import org.junit.jupiter.api.Assertions;
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

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private AccountService sut;

    @Mock
    private Database databaseMock;

    @BeforeEach
    public void init() throws SQLException {
        TestUtils.resetDb();
        sut = new AccountService(databaseMock);
        when(databaseMock.getConnection()).thenReturn(TestUtils.createTestConnection());
    }

    @Test
    @Tag("Integration")
    public void createAccount_WithValidData_ShouldInsertASingleAccount() throws SQLException {
        sut.createAccount("test");

        try (Connection conn = TestUtils.createTestConnection()) {
            assert conn != null;

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as rowcount FROM te_user WHERE id='test'");
            rs.next();

            int result = rs.getInt("rowcount");
            int expectedResult = 1;

            assertEquals(result, expectedResult);
        }
    }
}
