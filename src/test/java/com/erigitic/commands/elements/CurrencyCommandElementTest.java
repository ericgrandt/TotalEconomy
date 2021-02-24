package com.erigitic.commands.elements;

import com.erigitic.TestUtils;
import com.erigitic.domain.Balance;
import com.erigitic.domain.TECurrency;
import com.erigitic.services.TEEconomyService;
import ninja.leaping.configurate.ConfigurationNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class CurrencyCommandElementTest {
    private CurrencyCommandElement sut;

    @Mock
    private TEEconomyService economyServiceMock;

    @Mock
    private Player playerMock;

    @BeforeEach
    public void init() {
        sut = new CurrencyCommandElement(economyServiceMock, Text.of("test_key"));
    }

    @Test
    public void parseValue_WithInvalidCurrencyName_ShouldThrowArgumentParseException() {
        CommandArgs args = new CommandArgs("", new ArrayList<>());
        args.insertArg("invalid_currency");
        when(economyServiceMock.getCurrencies()).thenReturn(new HashSet<>());

        ArgumentParseException e = assertThrows(
            ArgumentParseException.class,
            () -> sut.parseValue(playerMock, args)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("Invalid currency");

        assertEquals(expectedResult, result);
    }

    @Test
    public void parseValue_WithValidCurrencyName_ShouldReturnCurrencyObject() throws ArgumentParseException {
        CommandArgs args = new CommandArgs("", new ArrayList<>());
        args.insertArg("valid_currency");

        Currency currency = new TECurrency(1, "valid_currency", "", "", true);
        Set<Currency> currencySet = new HashSet<>();
        currencySet.add(currency);
        when(economyServiceMock.getCurrencies()).thenReturn(currencySet);

        Currency result = sut.parseValue(playerMock, args);

        assertEquals(currency, result);
    }

    @Test
    public void complete_ShouldReturnAListOfStrings() {
        CommandArgs args = new CommandArgs("", new ArrayList<>());
        Set<Currency> currencySet = new HashSet<>();
        currencySet.add(new TECurrency(1, "valid_currency", "", "", true));
        currencySet.add(new TECurrency(2, "valid_currency2", "", "", false));
        when(economyServiceMock.getCurrencies()).thenReturn(currencySet);

        List<String> result = sut.complete(playerMock, args, new CommandContext());
        List<String> expectedResult = Arrays.asList("valid_currency", "valid_currency2");

        assertEquals(result, expectedResult);
    }
}
