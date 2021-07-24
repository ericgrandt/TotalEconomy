package com.ericgrandt.commands;

import com.ericgrandt.services.AccountService;
import com.ericgrandt.wrappers.CommandBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.PluginContainer;

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
    private EconomyService economyServiceMock;

    @Mock
    private AccountService accountServiceMock;

    @Mock
    private CommandBuilder commandBuilderMock;

    @Mock
    private RegisterCommandEvent<Command.Parameterized> registerCommandEventMock;

    @Test
    public void registerCommands_ShouldRegisterCommands() {
        Command.Builder builderMock = mock(Command.Builder.class, RETURNS_SELF);
        when(commandBuilderMock.getBuilder()).thenReturn(builderMock);
        when(registerCommandEventMock.register(any(), any(), any())).thenReturn(null);

        CommandRegister sut = spy(new CommandRegister(pluginMock, economyServiceMock, accountServiceMock, commandBuilderMock));

        sut.registerCommands(registerCommandEventMock);
        verify(sut, times(1)).registerBalanceCommand(any());
    }
}
