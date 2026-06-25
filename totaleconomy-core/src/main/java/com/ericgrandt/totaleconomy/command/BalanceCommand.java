package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.economy.EconomyProvider;
import com.ericgrandt.totaleconomy.mapper.ExceptionMapper;
import com.ericgrandt.totaleconomy.model.Player;
import com.ericgrandt.totaleconomy.model.Sender;
import net.kyori.adventure.text.Component;

import java.util.Map;

public class BalanceCommand implements Command {
    private final EconomyProvider economy;

    public BalanceCommand(EconomyProvider economy) {
        this.economy = economy;
    }

    @Override
    public CommandResult execute(Sender sender, Map<String, CommandArgument> args) {
        if (!(sender instanceof Player player)) {
            return CommandResult.FAILURE;
        }

        try {
            var currencyParam = args.get("currencyCode") instanceof StringArg(String value) ? value : null;
            var currency = currencyParam == null ? economy.getDefaultCurrency() : economy.getCurrency(currencyParam);
            var account = economy.getAccount(player.uniqueId(), currency.code());

            player.sendMessage(Component.text("Balance: ").append(currency.format(account.balance())));
        } catch (Exception e) {
            player.sendMessage(ExceptionMapper.getMessage(e));
            return CommandResult.FAILURE;
        }

        return CommandResult.SUCCESS;
    }

}
