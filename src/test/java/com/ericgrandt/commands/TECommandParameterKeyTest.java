package com.ericgrandt.commands;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Unit")
public class TECommandParameterKeyTest {
    @Test
    public void key_ShouldReturnCorrectKey() {
        TECommandParameterKey<String> sut = new TECommandParameterKey<>("mykey", String.class);

        String result = sut.key();
        String expected = "mykey";

        assertEquals(expected, result);
    }

    @Test
    public void type_WithTypeOfString_ShouldReturnStringClass() {
        TECommandParameterKey<String> sut = new TECommandParameterKey<>("mykey", String.class);

        Type result = sut.type();
        Type expected = String.class;

        assertEquals(expected, result);
    }

    @Test
    public void isInstance_WithMatchingClassType_ShouldReturnTrue() {
        TECommandParameterKey<String> sut = new TECommandParameterKey<>("mykey", String.class);

        boolean result = sut.isInstance("matching");

        assertTrue(result);
    }

    @Test
    public void isInstance_WithNonMatchingClassType_ShouldReturnFalse() {
        TECommandParameterKey<String> sut = new TECommandParameterKey<>("mykey", null);

        boolean result = sut.isInstance(1);

        assertFalse(result);
    }

    @Test
    public void cast_ShouldReturnUnsupportedOperationException() {
        TECommandParameterKey<String> sut = new TECommandParameterKey<>("mykey", String.class);

        assertThrows(
            UnsupportedOperationException.class,
            () -> sut.cast(1)
        );
    }
}
