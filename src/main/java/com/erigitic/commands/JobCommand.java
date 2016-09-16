/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class JobCommand implements CommandExecutor {
    private AccountManager accountManager;
    private TEJobs teJobs;

    public JobCommand(TotalEconomy totalEconomy) {
        accountManager = totalEconomy.getAccountManager();
        teJobs = totalEconomy.getTEJobs();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();

            if (args.getOne("jobName").isPresent()) {
                String jobName = args.getOne("jobName").get().toString().toLowerCase();

                teJobs.setJob(sender, jobName);
            } else {
                String jobName = teJobs.getPlayerJob(sender);

                sender.sendMessage(Text.of(TextColors.GRAY, "Your current job is: ", TextColors.GOLD, jobName));
                sender.sendMessage(Text.of(TextColors.GRAY, jobName, " Level: ", TextColors.GOLD, teJobs.getJobLevel(jobName, sender)));
                sender.sendMessage(Text.of(TextColors.GRAY, jobName, " Exp: ", TextColors.GOLD, teJobs.getJobExp(jobName, sender), "/", teJobs.getExpToLevel(sender), "\n"));
                sender.sendMessage(Text.of(TextColors.GRAY, "Available Jobs: ", TextColors.GOLD, teJobs.getJobList()));
            }
        }

        return CommandResult.success();
    }
}