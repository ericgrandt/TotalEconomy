package com.erigitic.commands;

import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.Shop;
import com.erigitic.shops.ShopItem;
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
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopCommand implements CommandExecutor {

    private TotalEconomy totalEconomy;
    private MessageManager messageManager;

    public ShopCommand(TotalEconomy totalEconomy, MessageManager messageManager) {
        this.totalEconomy = totalEconomy;
        this.messageManager = messageManager;
    }

    public CommandSpec getCommandSpec() {
        Add shopAddCommand = new Add(totalEconomy, messageManager);
        Buy shopBuyCommand = new Buy(totalEconomy, messageManager);

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

        private TotalEconomy totalEconomy;
        private MessageManager messageManager;

        public Add(TotalEconomy totalEconomy, MessageManager messageManager) {
            this.totalEconomy = totalEconomy;
            this.messageManager = messageManager;
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
            // Must be looking at your shop to add the item
            // Quantity must be between, and including, 1-64. If lower or higher, default to the min or max.
            Player player = ((Player) src).getPlayer().get();

            Optional<BlockRayHit<World>> optHit = BlockRay.from(player).skipFilter(BlockRay.blockTypeFilter(BlockTypes.CHEST)).distanceLimit(3).build().end();

            if (optHit.isPresent()) {
                BlockRayHit<World> hitBlock = optHit.get();
                Optional<TileEntity> tileEntityOpt = hitBlock.getLocation().getTileEntity();

                if (tileEntityOpt.isPresent()) {
                    Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

                    if (shopOpt.isPresent()) {
                        Shop shop = shopOpt.get();
                        List<ItemStack> stock = shop.getStock();

                        Optional<ItemStack> itemInHandOpt = player.getItemInHand(HandTypes.MAIN_HAND);

                        if (itemInHandOpt.isPresent()) {
                            ItemStack itemToStock = itemInHandOpt.get();
                            int itemInHandQuantity = itemToStock.getQuantity();

                            double price = args.<Double>getOne(Text.of("price")).get();
                            int quantity = args.<Integer>getOne(Text.of("quantity")).get();

                            if (hasQuantity(itemToStock, quantity)) {
                                ShopItem shopItem = new ShopItem(quantity, price);
                                ItemStack itemToRemove = ItemStack.empty();
                                itemToStock.setQuantity(1);

                                // Loop through the shop stock and check for duplicate item
                                for (ItemStack itemStack : stock) {
                                    if (itemStack.getItem().equals(itemToStock.getItem())) {
                                        if (itemStack.get(ShopKeys.SHOP_ITEM).get().getPrice() == price) {
                                            itemToRemove = itemStack;
                                            shopItem.setQuantity(shopItem.getQuantity() + itemStack.get(ShopKeys.SHOP_ITEM).get().getQuantity());
                                            break;
                                        }
                                    }
                                }

                                // Removes duplicate item from stock.
                                // This has to be done outside the above loop to avoid a ConcurrentModificationException.
                                if (itemToRemove != ItemStack.empty()) {
                                    stock.remove(itemToRemove);
                                }

                                itemToStock.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
                                itemToStock.offer(new ShopItemData(shopItem));

                                stock.add(itemToStock);
                                shop.setStock(stock);
                                tileEntityOpt.get().offer(new ShopData(shop));

                                updateHeldAmount(player, itemInHandQuantity - quantity);

                                player.sendMessage(Text.of("Item added to shop"));
                            } else {
                                throw new CommandException(Text.of("You do not have that many items to sell!"));
                            }
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
    }

    private class Buy implements CommandExecutor {

        private TotalEconomy totalEconomy;
        private MessageManager messageManager;

        public Buy(TotalEconomy totalEconomy, MessageManager messageManager) {
            this.totalEconomy = totalEconomy;
            this.messageManager = messageManager;
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

            Optional<BlockRayHit<World>> optHit = BlockRay.from(player).skipFilter(BlockRay.blockTypeFilter(BlockTypes.CHEST)).distanceLimit(3).build().end();

            if (optHit.isPresent()) {
                BlockRayHit<World> hitBlock = optHit.get();
                BlockType hitBlockType = hitBlock.getLocation().getBlockType();

                if (hitBlockType == BlockTypes.CHEST) {
                    List<ItemStack> stock = new ArrayList<>();

                    Shop shop = new Shop(player.getUniqueId(), player.getDisplayNameData().displayName().get().toPlain() + "'s Shop", stock);
                    ShopData shopData = new ShopData(shop);

                    hitBlock.getLocation().getTileEntity().get().offer(shopData);

                    player.sendMessage(Text.of("Shop purchased!"));
                }
            }

            return CommandResult.success();
        }
    }
}
