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
import com.erigitic.util.MessageManager;
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
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.*;

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
        Stock shopAddCommand = new Stock();
        Buy shopBuyCommand = new Buy();

        return CommandSpec.builder()
                .child(shopAddCommand.getCommandSpec(), "stock", "s")
                .child(shopBuyCommand.getCommandSpec(), "buy", "b")
                .permission("totaleconomy.command.shop")
                .executor(this)
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    private class Stock implements CommandExecutor {

        public Stock() {

        }

        public CommandSpec getCommandSpec() {
            return CommandSpec.builder()
                    .description(Text.of("Add an item to a shop"))
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
                Optional<Shop> shopOpt = tileEntity.get(ShopKeys.SINGLE_SHOP);

                if (shopOpt.isPresent()) {
                    Optional<ItemStack> itemInHandOpt = player.getItemInHand(HandTypes.MAIN_HAND);

                    if (itemInHandOpt.isPresent()) {
                        Shop shop = shopOpt.get();
                        ItemStack itemToStock = itemInHandOpt.get();

                        int quantity = args.<Integer>getOne(Text.of("quantity")).get();
                        double price = clampPrice(args.<Double>getOne(Text.of("price")).get(), shopManager.getMinPrice(), shopManager.getMaxPrice());

                        if (hasQuantity(itemToStock, quantity)) {
                            if (shop.hasEmptySlot()) {
                                itemToStock = prepareItemStackForShop(itemToStock, quantity, price);

                                shop.addItem(itemToStock);

                                tileEntity.offer(new ShopData(shop));

                                removeItemsFromHand(player, quantity);

                                Map<String, String> messageValues = new HashMap<>();
                                messageValues.put("quantity", String.valueOf(quantity));
                                messageValues.put("item", itemToStock.get(Keys.DISPLAY_NAME).orElse(Text.of(itemToStock.getTranslation())).toPlain());
                                messageValues.put("price", totalEconomy.getDefaultCurrency().format(BigDecimal.valueOf(price), 2).toPlain());

                                player.sendMessage(messageManager.getMessage("command.shop.stock.success", messageValues));
                            } else {
                                throw new CommandException(Text.of(messageManager.getMessage("command.shop.stock.noslots")));
                            }
                        } else {
                            throw new CommandException(Text.of(messageManager.getMessage("command.shop.stock.insufficientitems")));
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

        private void removeItemsFromHand(Player player, int numItemsToRemove) {
            ItemStack itemInHand = player.getItemInHand(HandTypes.MAIN_HAND).get();
            int numItemsInHand = itemInHand.getQuantity();

            itemInHand.setQuantity(numItemsInHand - numItemsToRemove);

            player.setItemInHand(HandTypes.MAIN_HAND, itemInHand);
        }

        private ItemStack prepareItemStackForShop(ItemStack itemStack, int quantity, double price) {
            ShopItem shopItem = new ShopItem(quantity, price);

            itemStack.setQuantity(1);
            itemStack.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
            itemStack.offer(new ShopItemData(shopItem));

            return itemStack;
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
                    .description(Text.of("Buy a shop"))
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
                            UUID creatorUUID = creatorOpt.get();

                            if (player.getUniqueId().equals(creatorUUID)) {
                                if (isChestEmpty(chest)) {
                                    TEAccount account = (TEAccount) accountManager.getOrCreateAccount(creatorUUID).get();
                                    TransactionResult transactionResult = account.withdraw(
                                            totalEconomy.getDefaultCurrency(),
                                            BigDecimal.valueOf(shopManager.getChestShopPrice()),
                                            Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer()))
                                    );

                                    if (transactionResult.getResult().equals(ResultType.SUCCESS)) {
                                        ShopData shopData = createShopDataFromPlayer(player);

                                        chest.offer(shopData);

                                        player.sendMessage(messageManager.getMessage("command.shop.buy.success"));
                                    } else {
                                        Map<String, String> messageValues = new HashMap<>();
                                        messageValues.put("price", totalEconomy.getDefaultCurrency().format(BigDecimal.valueOf(shopManager.getChestShopPrice())).toPlain());

                                        throw new CommandException(messageManager.getMessage("command.shop.buy.insufficientfunds", messageValues));
                                    }
                                } else { // Chest is not empty
                                    throw new CommandException(messageManager.getMessage("command.shop.buy.notempty"));
                                }
                            } else { // Chest was placed by someone other then the player executing the command
                                throw new CommandException(messageManager.getMessage("command.shop.buy.notowner"));
                            }
                        } else { // Chest was placed by someone other then the player executing the command
                            throw new CommandException(messageManager.getMessage("command.shop.buy.notowner"));
                        }
                    } else { // Chest has already been purchased
                        throw new CommandException(messageManager.getMessage("command.shop.buy.alreadypurchased"));
                    }
                }
            }

            return CommandResult.success();
        }

        private boolean isTileEntityAChest(TileEntity tileEntity) {
            BlockType blockType = tileEntity.getBlock().getType();

            if (blockType == BlockTypes.CHEST) {
                return true;
            }

            return false;
        }

        private boolean isChestEmpty(Chest chest) {
            int totalItems = chest.getInventory().totalItems();

            if (totalItems <= 0) {
                return true;
            }

            return false;
        }

        private ShopData createShopDataFromPlayer(Player player) {
            List<ItemStack> stock = new ArrayList<>();

            Shop shop = new Shop(player.getUniqueId(), player.getDisplayNameData().displayName().get().toPlain() + "'s Shop", stock);

            return new ShopData(shop);
        }
    }
}
