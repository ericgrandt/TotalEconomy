// package com.erigitic.commands;
//
// import com.erigitic.domain.Balance;
// import com.erigitic.domain.TECurrency;
// import com.erigitic.services.AccountService;
// import java.math.BigDecimal;
// import java.util.Optional;
//
// import net.kyori.adventure.text.Component;
// import org.spongepowered.api.command.CommandExecutor;
// import org.spongepowered.api.command.CommandResult;
// import org.spongepowered.api.command.exception.CommandException;
// import org.spongepowered.api.command.parameter.CommandContext;
// import org.spongepowered.api.command.parameter.Parameter;
// import org.spongepowered.api.entity.living.player.Player;
// import org.spongepowered.api.entity.living.player.server.ServerPlayer;
// import org.spongepowered.api.event.EventContext;
// import org.spongepowered.api.event.EventContextKeys;
// import org.spongepowered.api.service.economy.EconomyService;
// import org.spongepowered.api.service.economy.account.UniqueAccount;
//
// public class PayCommand implements CommandExecutor {
//     private final EconomyService economyService;
//     private final AccountService accountService;
//
//     public PayCommand(EconomyService economyService, AccountService accountService) {
//         this.economyService = economyService;
//         this.accountService = accountService;
//     }
//
//     @Override
//     public CommandResult execute(CommandContext context) throws CommandException {
//         if (!(context.cause().root() instanceof ServerPlayer)) {
//             throw new CommandException(Component.text("Only players can use this command"));
//         }
//
//         BigDecimal amount = getAmountArgument(context);
//         Player fromPlayer = (Player) context.cause().root();
//         Player toPlayer = getPlayerArgument(context);
//         if (toPlayer.uniqueId() == fromPlayer.uniqueId()) {
//             throw new CommandException(Component.text("You cannot pay yourself"));
//         }
//
//         UniqueAccount fromAccount = economyService.findOrCreateAccount(fromPlayer.uniqueId()).orElse(null);
//         UniqueAccount toAccount = economyService.findOrCreateAccount(toPlayer.uniqueId()).orElse(null);
//         if (fromAccount == null || toAccount == null) {
//             throw new CommandException(Component.text("Failed to run command: invalid account(s)"));
//         }
//
//         TECurrency defaultCurrency = (TECurrency) economyService.defaultCurrency();
//         EventContext eventContext = EventContext.builder()
//             .add(EventContextKeys.PLAYER, fromPlayer)
//             .build();
//         fromAccount.transfer(toAccount, defaultCurrency, amount);
//
//         Balance fromBalance = new Balance(
//             fromAccount.uniqueId(),
//             defaultCurrency.getIntId(),
//             fromAccount.balance(defaultCurrency)
//         );
//         Balance toBalance = new Balance(
//             toAccount.uniqueId(),
//             defaultCurrency.getIntId(),
//             toAccount.balance(defaultCurrency)
//         );
//
//         boolean isSuccessful = accountService.setTransferBalances(fromBalance, toBalance);
//
//         if (!isSuccessful) {
//             throw new CommandException(Component.text("Failed to run command: unable to set balances"));
//         }
//
//         return CommandResult.success();
//     }
//
//     private BigDecimal getAmountArgument(CommandContext args) throws CommandException {
//         Optional<BigDecimal> amountOpt = args.one(Parameter.bigDecimal().key("amount").build());
//         if (!amountOpt.isPresent()) {
//             throw new CommandException(Component.text("Amount argument is missing"));
//         } else if (!(amountOpt.get().compareTo(BigDecimal.ZERO) > 0)) {
//             throw new CommandException(Component.text("Amount must be greater than 0"));
//         }
//
//         return amountOpt.get();
//     }
//
//     private Player getPlayerArgument(CommandContext args) throws CommandException {
//         Optional<ServerPlayer> toPlayerOpt = args.one(Parameter.player().key("player").build());
//         if (!toPlayerOpt.isPresent()) {
//             throw new CommandException(Component.text("Player argument is missing"));
//         }
//
//         return toPlayerOpt.get();
//     }
// }
