package com.ericgrandt.totaleconomy.wrappers;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.transaction.Operation;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKey;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.DefaultedRegistryType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

// TODO: Convert these to variables where appropriate. I don't think we need them to be
//  functions
public class SpongeWrapper {
    public CommandResult success() {
        return CommandResult.success();
    }

    public CommandResult error(Component errorMessage) {
        return CommandResult.error(errorMessage);
    }

    public TransactionType deposit() {
        return TransactionTypes.DEPOSIT.get();
    }

    public TransactionType withdraw() {
        return TransactionTypes.WITHDRAW.get();
    }

    public TransactionType transfer() {
        return TransactionTypes.TRANSFER.get();
    }

    public Parameter.Value<Double> doubleParameter(String key) {
        return Parameter.doubleNumber().key(key).build();
    }

    public Parameter.Value<ServerPlayer> playerParameter(String key) {
        return Parameter.player().key(key).build();
    }

    public Key<Value<Integer>> growthStage() {
        return Keys.GROWTH_STAGE;
    }

    public Key<Value<Integer>> maxGrowthStage() {
        return Keys.MAX_GROWTH_STAGE;
    }

    public DefaultedRegistryType<ItemType> itemType() {
        return RegistryTypes.ITEM_TYPE;
    }

    public DefaultedRegistryType<BlockType> blockType() {
        return RegistryTypes.BLOCK_TYPE;
    }

    public DefaultedRegistryType<EntityType<?>> entityType() {
        return RegistryTypes.ENTITY_TYPE;
    }

    public EventContextKey<ItemStackSnapshot> usedItem() {
        return EventContextKeys.USED_ITEM;
    }

    public Operation breakOperation() {
        return Operations.BREAK.get();
    }

    public Operation placeOperation() {
        return Operations.PLACE.get();
    }
}
