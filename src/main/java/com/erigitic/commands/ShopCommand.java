package com.erigitic.commands;

import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.Shop;
import com.erigitic.shops.ShopItem;
import com.erigitic.shops.ShopManager;
import com.erigitic.shops.data.ShopData;
import com.erigitic.shops.data.ShopItemData;
import com.erigitic.shops.data.ShopKeys;
import com.erigitic.util.MessageManager;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopCommand implements CommandExecutor {

    private TotalEconomy totalEconomy;
    private ShopManager shopManager;
    private MessageManager messageManager;

    public ShopCommand(TotalEconomy totalEconomy, ShopManager shopManager, MessageManager messageManager) {
        this.totalEconomy = totalEconomy;
        this.shopManager = shopManager;
        this.messageManager = messageManager;
    }

    public CommandSpec getCommandSpec() {
        Add shopAddCommand = new Add();
        Buy shopBuyCommand = new Buy();

        return CommandSpec.builder()
                .child(shopAddCommand.getCommandSpec(), "add", "a")
                .child(shopBuyCommand.getCommandSpec(), "buy", "b")
                .permission("totaleconomy.command.shop")
                .executor(this)
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        return CommandResult.success();
    }

    private class Add implements CommandExecutor {

        public Add() {

        }

        public CommandSpec getCommandSpec() {
            return CommandSpec.builder()
                    .description(Text.of("Add an item to a shop"))
                    .permission("totaleconomy.command.shop.add")
                    .executor(this)
                    .arguments(
                            GenericArguments.doubleNum(Text.of("price")),
                            GenericArguments.integer(Text.of("quantity"))
                    )
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Player player = ((Player) src).getPlayer().get();

            Optional<TileEntity> tileEntityOpt = shopManager.getTileEntityFromRaycast(player);

            if (tileEntityOpt.isPresent()) {
                TileEntity tileEntity = tileEntityOpt.get();
                Optional<Shop> shopOpt = tileEntity.get(ShopKeys.SINGLE_SHOP);

                if (shopOpt.isPresent()) {
                    Optional<ItemStack> itemInHandOpt = player.getItemInHand(HandTypes.MAIN_HAND);

                    if (itemInHandOpt.isPresent()) {
                        Shop shop = shopOpt.get();
                        ItemStack itemToStock = itemInHandOpt.get();

                        int itemInHandQuantity = itemToStock.getQuantity();

                        double price = args.<Double>getOne(Text.of("price")).get();
                        int quantity = args.<Integer>getOne(Text.of("quantity")).get();

                        if (hasQuantity(itemToStock, quantity)) {
                            itemInHandQuantity -= quantity;
                            itemToStock.setQuantity(1);

                            shop = addItemToShop(shop, itemToStock, quantity, price);

                            tileEntity.offer(new ShopData(shop));

                            updateHeldAmount(player, itemInHandQuantity);

                            player.sendMessage(Text.of(
                                    TextColors.GRAY, "You've added ",
                                    TextColors.GOLD, quantity, "x", itemToStock.getItem().getName().split(":")[1],
                                    TextColors.GRAY, " priced at ",
                                    TextColors.GOLD, totalEconomy.getDefaultCurrency().format(BigDecimal.valueOf(price), 2),
                                    TextColors.GRAY, " each."));

                            // TODO: I'll deal with this later as it is more of a visual thing then anything else
                            // Loop through the shop stock and check for duplicate item
//                            for (ItemStack itemStack : shopStock) {
//                                if (itemStack.getItem().equals(itemToStock.getItem())) {
//                                    if (itemStack.get(ShopKeys.SHOP_ITEM).get().getPrice() == price) {
//                                        ItemStack itemToRemove = itemStack; // Store duplicate item in a different variable to avoid a ConcurrentModificationException
//
//                                        shopStock.remove(itemToRemove);
//                                        quantity += itemStack.get(ShopKeys.SHOP_ITEM).get().getQuantity();
//
//                                        // If the quantity is over the max stack size, split it up
//                                        if (quantity > 64) {
//                                            ItemStack excessItemStack = itemToStock.copy();
//                                            int excessQuantity = quantity - 64;
//                                            excess = true;
//                                            quantity = 64;
//
//                                            shop = addItemToShop(shop, itemToStock, quantity, price);
//                                            shop = addItemToShop(shop, excessItemStack, excessQuantity, price);
//                                        }
//
//                                        break;
//                                    }
//                                }
//                            }
//
//                            if (!excess) {
//                                shop = addItemToShop(shop, itemToStock, quantity, price);
//                            }
                        } else {
                            throw new CommandException(Text.of("You do not have that many items to sell!"));
                        }
                    }
                }
            }

            return CommandResult.success();
        }

        private boolean hasQuantity(ItemStack itemInHand, int quantity) {
            if (itemInHand.getQuantity() >= quantity) {
                return true;
            }

            return false;
        }

        private void updateHeldAmount(Player player, int quantity) {
            ItemStack itemInHand = player.getItemInHand(HandTypes.MAIN_HAND).get();
            itemInHand.setQuantity(quantity);

            player.setItemInHand(HandTypes.MAIN_HAND, itemInHand);
        }

        /**
         * Add an item to a shop's stock
         *
         * @param shop The shop to add an item to
         * @param itemToStock The item to add
         * @param quantity The amount to add
         * @param price The price of the item
         * @return The updated shop
         */
        private Shop addItemToShop(Shop shop, ItemStack itemToStock, int quantity, double price) {
            List<ItemStack> shopStock = shop.getStock();
            ShopItem shopItem = new ShopItem(quantity, price);

            itemToStock.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
            itemToStock.offer(new ShopItemData(shopItem));

            shopStock.add(itemToStock);
            shop.setStock(shopStock);

            return shop;
        }
    }

    private class Buy implements CommandExecutor {

        public Buy() {

        }

        public CommandSpec getCommandSpec() {
            return CommandSpec.builder()
                    .description(Text.of("Buy a shop"))
                    .permission("totaleconomy.command.shop.buy")
                    .executor(this)
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Player player = ((Player) src).getPlayer().get();

            Optional<TileEntity> tileEntityOpt = shopManager.getTileEntityFromRaycast(player);

            if (tileEntityOpt.isPresent()) {
                TileEntity tileEntity = tileEntityOpt.get();
                BlockType blockType = tileEntity.getBlock().getType();

                if (blockType == BlockTypes.CHEST) {
                    List<ItemStack> stock = new ArrayList<>();

                    Shop shop = new Shop(player.getUniqueId(), player.getDisplayNameData().displayName().get().toPlain() + "'s Shop", stock);
                    ShopData shopData = new ShopData(shop);

                    tileEntity.offer(shopData);

                    player.sendMessage(Text.of("Shop purchased!"));
                }
            }

            return CommandResult.success();
        }
    }
}
