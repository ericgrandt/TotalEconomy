package com.ericgrandt.commands;

import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.CommandBuilder;
import com.ericgrandt.wrappers.ParameterWrapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.PluginContainer;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class CommandRegisterTest {
    @Mock
    private PluginContainer pluginMock;

    @Mock
    private TEEconomyService economyServiceMock;

    @Mock
    private AccountService accountServiceMock;

    @Mock
    private CommandBuilder commandBuilderMock;

    @Mock
    private RegisterCommandEvent<Command.Parameterized> registerCommandEventMock;

    @Mock
    private ParameterWrapper parameterWrapperMock;

    @Test
    public void registerCommands_ShouldRegisterCommands() {
        Command.Builder builderMock = mock(Command.Builder.class, RETURNS_SELF);
        Parameter.Value.Builder parameterMock = mock(Parameter.Value.Builder.class, RETURNS_SELF);
        when(commandBuilderMock.getBuilder()).thenReturn(builderMock);
        when(registerCommandEventMock.register(any(), any(), any())).thenReturn(null);
        when(parameterWrapperMock.player()).thenReturn(parameterMock);
        when(parameterWrapperMock.bigDecimal()).thenReturn(parameterMock);
        when(parameterWrapperMock.currency()).thenReturn(parameterMock);

        CommandRegister sut = spy(
            new CommandRegister(
                pluginMock,
                economyServiceMock,
                accountServiceMock,
                commandBuilderMock,
                parameterWrapperMock
            )
        );

        sut.registerCommands(registerCommandEventMock);
        verify(sut, times(1)).registerBalanceCommand(any());
        verify(sut, times(1)).registerPayCommand(any());
    }
}
