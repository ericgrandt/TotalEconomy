package com.ericgrandt.commands;

import com.ericgrandt.TotalEconomy;
import com.ericgrandt.config.LocalePaths;
import com.ericgrandt.config.Locales.Placeholders;
import com.ericgrandt.data.TempData;
import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TECommandResult;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.ParameterWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.locale.LocaleSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BalanceAddCommand implements CommandExecutor {
    private final TEEconomyService economyService;
    private final AccountService accountService;
    private final ParameterWrapper parameterWrapper;
    private final TotalEconomy plugin = TotalEconomy.getInstance();

    public BalanceAddCommand(TEEconomyService economyService, AccountService accountService, ParameterWrapper parameterWrapper) {
        this.economyService = economyService;
        this.accountService = accountService;
        this.parameterWrapper = parameterWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) throws CommandException {

        Optional<User> player = null;
        try {
            player = getUserArgument(context);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        TECurrency currency = getCurrencyArgument(context);
        BigDecimal amount = getAmountArgument(context);
        if(!player.isPresent()) {
            plugin.getDefaultConfiguration().get().writeTempData(getUnknownUserArgument(context), new TempData(currency, amount));
            throw new CommandException(plugin.getLocales().getText("Account not found. Writing temporary data.", ((LocaleSource) context.cause().root()).locale(), LocalePaths.WRITE_TEMP_DATA));
        }
        Balance balance = accountService.getBalance(
            player.get().uniqueId(),
            currency.getId()
        );
        balance.setBalance(balance.getBalance().add(amount));
        accountService.getAccountData().setBalance(balance);
        player.get().player().ifPresent(online -> {
        	online.sendMessage(plugin.getLocales().getTextWhithReplacers("&aYour balance in the currency &6" + Placeholders.CURRENCY + " &ais increased by &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Current balance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a.", online.locale(), plugin.getLocales().createReplaceMap(Arrays.asList(Placeholders.CURRENCY, Placeholders.VALUE, Placeholders.BALANCE), Arrays.asList(toPlain(currency.symbol()), amount.doubleValue(), balance.getBalance().doubleValue())), LocalePaths.BALANCE_ADD));
        });
        context.cause().audience().sendMessage(plugin.getLocales().getTextWhithReplacers("&aThe balance of the player &6" + Placeholders.PLAYER + " &aincreased by &6" + Placeholders.CURRENCY + Placeholders.VALUE + "&a. Current balance&f: &6" + Placeholders.CURRENCY + Placeholders.BALANCE + "&a.", ((LocaleSource) context.cause().root()).locale(), plugin.getLocales().createReplaceMap(Arrays.asList(Placeholders.PLAYER, Placeholders.CURRENCY, Placeholders.VALUE, Placeholders.BALANCE), Arrays.asList(player.get().name(), toPlain(currency.symbol()), amount.doubleValue(), balance.getBalance().doubleValue())), LocalePaths.BALANCE_ADD_ADMIN));
        /*context.cause().audience().sendMessage(
            Component.text("New balance: ", NamedTextColor.GRAY)
                .append(
                    currency.format(balance.getBalance()).color(NamedTextColor.GOLD)
                )
        );
*/
        return new TECommandResult(true);
    }

    private TECurrency getCurrencyArgument(CommandContext context) throws CommandException {
        Parameter.Key<String> currencyKey = parameterWrapper.key("currency", String.class);
        Optional<String> currencyParamOpt = context.one(currencyKey);
        if (currencyParamOpt.isPresent()) {
            Optional<Currency> currencyOpt = economyService.currencies().stream().filter(c ->
                PlainTextComponentSerializer.plainText().serialize(c.displayName()).equals(currencyParamOpt.get())
            ).findFirst();

            if (currencyOpt.isPresent()) {
                return (TECurrency) currencyOpt.get();
            } else {
                throw new CommandException(plugin.getLocales().getText("That currency does not exist", ((LocaleSource) context.cause().root()).locale(), LocalePaths.CURRENCY_NOT_EXIST));
            }
        }

        return (TECurrency) economyService.defaultCurrency();
    }

    private BigDecimal getAmountArgument(CommandContext context) throws CommandException {
        Parameter.Key<BigDecimal> amountKey = parameterWrapper.key("amount", BigDecimal.class);
        Optional<BigDecimal> amountOpt = context.one(amountKey);
        if (!amountOpt.isPresent()) {
            throw new CommandException(plugin.getLocales().getText("Amount argument is missing.", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PAY_MISSING_AMOUNT));
        } else if (!(amountOpt.get().compareTo(BigDecimal.ZERO) > 0)) {
            throw new CommandException(plugin.getLocales().getText("Amount must be greater than 0.", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PAY_ZERO_OR_BELOW));
        }

        return amountOpt.get();
    }

    private Optional<User> getUserArgument(CommandContext context) throws CommandException, ExecutionException, InterruptedException {
        return Sponge.server().userManager().load(getUnknownUserArgument(context)).get();
    }

    private String getUnknownUserArgument(CommandContext context) throws CommandException {
        Parameter.Key<String> playerKey = parameterWrapper.key("player", String.class);
        Optional<String> toUserOpt = context.one(playerKey);
        if (!toUserOpt.isPresent()) {
            throw new CommandException(plugin.getLocales().getText("Player argument is missing.", ((LocaleSource) context.cause().root()).locale(), LocalePaths.PLAYER_MISSING));
        }
        return toUserOpt.get();
    }

    private String toPlain(Component component) {
    	return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
}