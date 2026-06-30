package com.ericgrandt.totaleconomy.mapper;

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Verify that we're only logging for actual system errors, and not user caused errors
 */
@ExtendWith(MockitoExtension.class)
public class CommandExceptionMapperTest {
    @Mock
    private Logger loggerMock;

    @Test
    public void handleException_WithCurrencyNotFoundException_ShouldNotLog() {
        // Arrange
        var sut = new CommandExceptionMapper(loggerMock);

        // Act
        sut.handleException(new CurrencyNotFoundException());

        // Assert
        verifyNoInteractions(loggerMock);
    }

    @Test
    public void handleException_WithAccountNotFoundException_ShouldNotLog() {
        // Arrange
        var sut = new CommandExceptionMapper(loggerMock);

        // Act
        sut.handleException(new AccountNotFoundException());

        // Assert
        verifyNoInteractions(loggerMock);
    }

    @Test
    public void handleException_WithMissingDefaultCurrencyException_ShouldLog() {
        // Arrange
        var sut = new CommandExceptionMapper(loggerMock);

        // Act
        sut.handleException(new MissingDefaultCurrencyException());

        // Assert
        verify(loggerMock, times(1)).error(any(), any(Throwable.class));
    }

    @Test
    public void handleException_WithUnhandledException_ShouldLog() {
        // Arrange
        var sut = new CommandExceptionMapper(loggerMock);

        // Act
        sut.handleException(new IllegalArgumentException());

        // Assert
        verify(loggerMock, times(1)).error(any(), any(Throwable.class));
    }
}
