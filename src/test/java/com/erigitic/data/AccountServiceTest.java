package com.erigitic.data;

import com.erigitic.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private AccountService sut;

    @Mock
    private Database databaseMock;

    @BeforeEach
    public void init() throws SQLException {
        sut = new AccountService(databaseMock);

        when(databaseMock.getConnection()).thenReturn(TestUtils.createTestConnection());
    }

    @Test
    @Tag("Integration")
    public void testTest() {
        sut.createAccount("test");
    }
}
