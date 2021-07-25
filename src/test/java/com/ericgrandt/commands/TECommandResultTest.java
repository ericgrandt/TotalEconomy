package com.ericgrandt.commands;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Unit")
public class TECommandResultTest {
    @Test
    public void isSuccess_WithValueOfTrue_ShouldReturnTrue() {
        TECommandResult sut = new TECommandResult(true);

        boolean result = sut.isSuccess();

        assertTrue(result);
    }

    @Test
    public void result_WithSuccess_ShouldReturnZero() {
        TECommandResult sut = new TECommandResult(true);

        int result = sut.result();
        int expectedResult = 0;

        assertEquals(expectedResult, result);
    }

    @Test
    public void result_WithoutSuccess_ShouldReturnOne() {
        TECommandResult sut = new TECommandResult(false);

        int result = sut.result();
        int expectedResult = 1;

        assertEquals(expectedResult, result);
    }

    @Test
    public void errorMessage_ShouldReturnUnsupportedOperationException() {
        TECommandResult sut = new TECommandResult(false);

        assertThrows(
            UnsupportedOperationException.class,
            sut::errorMessage
        );
    }
}
