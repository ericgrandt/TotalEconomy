package com.erigitic.commands;

import com.erigitic.TotalEconomy;
import com.erigitic.shop.data.MutableShopData;
import com.erigitic.shop.data.ShopKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class DebugCommand implements CommandExecutor {
    private final TotalEconomy plugin;

    public DebugCommand() {
        plugin = TotalEconomy.getPlugin();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only players can use this command"));
        }

        World world = ((Player) src).getWorld();
        Villager villager = (Villager) world.createEntity(EntityTypes.VILLAGER, ((Player) src).getPosition());

        MutableShopData shopData = villager.getOrCreate(MutableShopData.class).get();
        shopData.set(ShopKeys.SHOP_ID, "12345");
        villager.offer(shopData);
        plugin.getLogger().debug("" + villager.get(ShopKeys.SHOP_ID).get());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            world.spawnEntity(villager);
        }

        return CommandResult.success();
    }
}
