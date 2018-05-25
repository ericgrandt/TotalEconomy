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
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.Shop;
import com.erigitic.shops.ShopItem;
import com.erigitic.shops.ShopManager;
import com.erigitic.shops.data.ShopData;
import com.erigitic.shops.data.ShopItemData;
import com.erigitic.shops.data.ShopKeys;
import com.erigitic.util.InventoryUtils;
import com.erigitic.util.MessageManager;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
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
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class ShopCommand implements CommandExecutor {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private ShopManager shopManager;
    private MessageManager messageManager;

    public ShopCommand(TotalEconomy totalEconomy, AccountManager accountManager, ShopManager shopManager, MessageManager messageManager) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.shopManager = shopManager;
        this.messageManager = messageManager;
    }

    public CommandSpec getCommandSpec() {
        Stock shopStockCommand = new Stock();
        Buy shopBuyCommand = new Buy();

        return CommandSpec.builder()
                .child(shopStockCommand.getCommandSpec(), "stock", "s")
                .child(shopBuyCommand.getCommandSpec(), "buy", "b")
                .permission("totaleconomy.command.shop")
                .executor(this)
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    private boolean isTileEntityAChest(TileEntity tileEntity) {
        BlockType blockType = tileEntity.getBlock().getType();

        if (blockType == BlockTypes.CHEST) {
            return true;
        }

        return false;
    }

    private class Stock implements CommandExecutor {

        public Stock() {

        }

        public CommandSpec getCommandSpec() {
            return CommandSpec.builder()
                    .description(Text.of("Stock a shop with the currently held item"))
                    .permission("totaleconomy.command.shop.stock")
                    .executor(this)
                    .arguments(
                            GenericArguments.integer(Text.of("quantity")),
                            GenericArguments.doubleNum(Text.of("price"))
                    )
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Player player = ((Player) src).getPlayer().get();

            Optional<TileEntity> tileEntityOpt = shopManager.getTileEntityFromPlayerRaycast(player);

            if (tileEntityOpt.isPresent()) {
                TileEntity tileEntity = tileEntityOpt.get();

                if (isTileEntityAChest(tileEntity)) {
                    Chest chest = (Chest) tileEntity;
                    Optional<Shop> shopOpt = chest.get(ShopKeys.SINGLE_SHOP);

                    if (shopOpt.isPresent()) {
                        Optional<ItemStack> itemInHandOpt = player.getItemInHand(HandTypes.MAIN_HAND);
                        UUID shopOwner = shopOpt.get().getOwner();

                        if (shopOwner.equals(player.getUniqueId()) && itemInHandOpt.isPresent()) {
                            ItemStack itemInHand = itemInHandOpt.get();

                            int quantity = args.<Integer>getOne(Text.of("quantity")).get();
                            double price = clampPrice(args.<Double>getOne(Text.of("price")).get(), shopManager.getMinPrice(), shopManager.getMaxPrice());

                            if (quantity > itemInHand.getMaxStackQuantity()) {
                                quantity = itemInHand.getMaxStackQuantity();
                            }

                            Inventory playerInventory = player.getInventory();
                            int itemAmountInInventory = InventoryUtils.getItemAmountInInventory(playerInventory, itemInHand);

                            if (itemAmountInInventory >= quantity) {
                                ItemStack preparedItem = prepareItemStackForShop(itemInHand, quantity, price);

                                Collection<ItemStackSnapshot> rejectedItems = chest.getInventory().offer(preparedItem.copy()).getRejectedItems();

                                if (rejectedItems.size() <= 0) {
                                    InventoryUtils.removeItem(playerInventory, itemInHand, quantity);

                                    Map<String, String> messageValues = new HashMap<>();
                                    messageValues.put("quantity", String.valueOf(quantity));
                                    messageValues.put("item", preparedItem.get(Keys.DISPLAY_NAME).orElse(Text.of(preparedItem.getTranslation())).toPlain());
                                    messageValues.put("price", totalEconomy.getDefaultCurrency().format(BigDecimal.valueOf(price), 2).toPlain());

                                    player.sendMessage(messageManager.getMessage("command.shop.stock.success", messageValues));
                                } else {
                                    throw new CommandException(Text.of(messageManager.getMessage("command.shop.stock.noslots")));
                                }
                            } else {
                                throw new CommandException(Text.of(messageManager.getMessage("command.shop.stock.insufficientitems")));
                            }
                        } else {
                            throw new CommandException((Text.of(messageManager.getMessage("command.shop.stock.notowner"))));
                        }
                    }
                }
            }

            return CommandResult.success();
        }

        /**
         * Prepares an ItemStack to be stocked in a shop. Sets the quantity to 1, adds lore, and adds ShopItemData.
         *
         * @param itemStack The ItemStack to prepare
         * @param quantity The quantity being stocked
         * @param price The price of the ItemStack being stocked
         * @return ItemStack An ItemStack that is prepared to be stocked in a shop
         */
        private ItemStack prepareItemStackForShop(ItemStack itemStack, int quantity, double price) {
            ShopItem shopItem = new ShopItem(price);
            ItemStack preparedItem = itemStack.copy();

            preparedItem.setQuantity(quantity);
            preparedItem.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
            preparedItem.offer(new ShopItemData(shopItem));

            return preparedItem;
        }

        private double clampPrice(double price, double minValue, double maxValue) {
            if (price < minValue) {
                return minValue;
            } else if (price > maxValue) {
                return maxValue;
            }

            return price;
        }
    }

    private class Buy implements CommandExecutor {

        public Buy() {

        }

        public CommandSpec getCommandSpec() {
            return CommandSpec.builder()
                    .description(Text.of("Buy a chest shop"))
                    .permission("totaleconomy.command.shop.buy")
                    .executor(this)
                    .build();
        }

        @Override
        public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
            Player player = ((Player) src).getPlayer().get();

            Optional<TileEntity> tileEntityOpt = shopManager.getTileEntityFromPlayerRaycast(player);

            if (tileEntityOpt.isPresent()) {
                TileEntity tileEntity = tileEntityOpt.get();

                if (isTileEntityAChest(tileEntity)) {
                    Chest chest = (Chest) tileEntity;
                    BlockSnapshot blockSnapshot = chest.getLocation().createSnapshot();
                    Optional<UUID> creatorOpt = blockSnapshot.getCreator();

                    // If double chest, throw exception
                    if (chest.getDoubleChestInventory().isPresent()) {
                        throw new CommandException(messageManager.getMessage("command.shop.buy.doublechest"));
                    }

                    if (!chest.get(ShopKeys.SINGLE_SHOP).isPresent()) {
                        if (creatorOpt.isPresent()) {
                            UUID creatorUniqueId = creatorOpt.get();

                            if (player.getUniqueId().equals(creatorUniqueId)) {
                                if (isChestEmpty(chest)) {
                                    TEAccount account = (TEAccount) accountManager.getOrCreateAccount(creatorUniqueId).get();

                                    Cause cause = Cause.builder()
                                            .append(player)
                                            .append(totalEconomy.getPluginContainer())
                                            .build(EventContext.empty());

                                    TransactionResult transactionResult = account.withdraw(
                                            totalEconomy.getDefaultCurrency(),
                                            BigDecimal.valueOf(shopManager.getChestShopPrice()),
                                            cause
                                    );

                                    if (transactionResult.getResult().equals(ResultType.SUCCESS)) {
                                        Shop shop = createShopFromPlayer(player);

                                        chest.offer(Keys.DISPLAY_NAME, Text.of(TextStyles.BOLD, TextColors.BLUE, shop.getTitle()));
                                        chest.offer(new ShopData(shop));

                                        player.sendMessage(messageManager.getMessage("command.shop.buy.success"));
                                    } else {
                                        Map<String, String> messageValues = new HashMap<>();
                                        messageValues.put("price", totalEconomy.getDefaultCurrency().format(BigDecimal.valueOf(shopManager.getChestShopPrice())).toPlain());

                                        throw new CommandException(messageManager.getMessage("command.shop.buy.insufficientfunds", messageValues));
                                    }
                                } else { // Chest is not empty
                                    throw new CommandException(messageManager.getMessage("command.shop.buy.notempty"));
                                }
                            } else { // Chest was placed by someone other than the player executing the command
                                throw new CommandException(messageManager.getMessage("command.shop.buy.notowner"));
                            }
                        } else { // Chest was placed by someone other than the player executing the command
                            throw new CommandException(messageManager.getMessage("command.shop.buy.notowner"));
                        }
                    } else { // Chest has already been purchased
                        throw new CommandException(messageManager.getMessage("command.shop.buy.alreadypurchased"));
                    }
                }
            }

            return CommandResult.success();
        }

        private boolean isChestEmpty(Chest chest) {
            int totalItems = chest.getInventory().totalItems();

            if (totalItems <= 0) {
                return true;
            }

            return false;
        }

        private Shop createShopFromPlayer(Player player) {
            Shop shop = new Shop(player.getUniqueId(), player.getDisplayNameData().displayName().get().toPlain() + "'s Shop");

            return shop;
        }
    }
}
