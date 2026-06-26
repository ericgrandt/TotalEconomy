package com.ericgrandt.totaleconomy.command

// class BalanceCommandExecutor(
//    private val scope: CoroutineScope,
//    private val balanceCommand: BalanceCommand,
// ) : CommandExecutor {
//    override fun onCommand(
//        sender: CommandSender,
//        command: Command,
//        label: String,
//        args: Array<out String>,
//    ): Boolean {
//        if (sender !is Player) {
//            return false
//        }
//
//        // TODO: Double check this logic first before calling it done
//        var commandArgs = emptyMap<String, CommandArgument>()
//        if (!args.isEmpty()) {
//            commandArgs =
//                mapOf(
//                    "currencyCode" to CommandArgument.StringParam(args[0]),
//                )
//        }
//
//        scope.launch {
//            balanceCommand.execute(PaperPlayer(sender), commandArgs)
//        }
//
//        return true
//    }
// }
