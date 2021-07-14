package com.ericgrandt.commands;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
    public void result_ShouldReturnUnsupportedOperationException() {
        TECommandResult sut = new TECommandResult(true);

        assertThrows(
            UnsupportedOperationException.class,
            sut::result
        );
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
