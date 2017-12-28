package com.erigitic.shops;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.data.ShopData;
import com.erigitic.shops.data.ShopItemData;
import com.erigitic.shops.data.ShopKeys;
import com.erigitic.util.MessageManager;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.*;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.*;

public class ShopManager {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private MessageManager messageManager;

    private final double minPrice;
    private final double maxPrice;
    private final double chestShopPrice;

    public ShopManager(TotalEconomy totalEconomy, AccountManager accountManager, MessageManager messageManager) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.messageManager = messageManager;

        minPrice = this.totalEconomy.getShopNode().getNode("min-item-price").getDouble();
        maxPrice = this.totalEconomy.getShopNode().getNode("max-item-price").getDouble();
        chestShopPrice = this.totalEconomy.getShopNode().getNode("chestshop", "price").getDouble();
    }

    /**
     * Handles a user left clicking (purchasing) an item from a shop.
     *
     * @param event
     * @param player The player clicking within an inventory
     * @param inventory The inventory being clicked
     */
    // TODO: Put some of this code in a separate function as it will be reused in the ClickInventoryEvent.Secondary function
    @Listener
    @Exclude(ClickInventoryEvent.Shift.class)
    public void onItemPurchase(ClickInventoryEvent.Primary event, @First Player player, @Getter("getTargetInventory") Inventory inventory) {
        Optional<TileEntity> tileEntityOpt = getTileEntityFromPlayerRaycast(player);

        if (tileEntityOpt.isPresent()) {
            Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                Shop shop = shopOpt.get();
                Slot clickedSlot = event.getTransactions().get(0).getSlot();
                ItemStack clickedItem = ItemStack.builder().fromSnapshot(event.getCursorTransaction().getDefault()).build();
                Optional<ShopItem> shopItemOpt = clickedItem.get(ShopKeys.SHOP_ITEM);

                if (shopItemOpt.isPresent()) {
                    event.getCursorTransaction().setValid(false);

                    ShopItem shopItem = shopItemOpt.get();
                    TEAccount ownerAccount = (TEAccount) accountManager.getOrCreateAccount(shop.getOwner()).get();
                    TEAccount customerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                    if (customerAccount.getBalance(totalEconomy.getDefaultCurrency()).doubleValue() >= shopItem.getPrice()) {
                        removeShopItemData(clickedItem);

                        ItemStack purchasedItem = clickedItem.copy();
                        purchasedItem.setQuantity(1);

                        Collection<ItemStackSnapshot> rejectedItems = player.getInventory().query(GridInventory.class, Hotbar.class).offer(purchasedItem).getRejectedItems();

                        if (rejectedItems.size() <= 0) {
                            customerAccount.transfer(ownerAccount, totalEconomy.getDefaultCurrency(), BigDecimal.valueOf(shopItem.getPrice()), Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                            shopItem.setQuantity(shopItem.getQuantity() - 1);

                            updateItemInSlot(shopItem, clickedSlot, clickedItem);

                            shop.setStock(getShopStockFromInventory(inventory));
                            tileEntityOpt.get().offer(new ShopData(shop));
                        } else {
                            event.getTransactions().get(0).setValid(false);

                            player.sendMessage(messageManager.getMessage("shops.purchase.noroom"));
                        }
                    } else {
                        event.getTransactions().get(0).setValid(false);

                        player.sendMessage(messageManager.getMessage("shops.purchase.insufficientfunds"));
                    }
                } else {
                    event.getTransactions().get(0).setValid(false);
                }
            }
        }
    }

    @Listener
    public void onShopSecondaryClick(ClickInventoryEvent.Secondary event, @First Player player) {
        Optional<TileEntity> tileEntityOpt = getTileEntityFromPlayerRaycast(player);

        if (tileEntityOpt.isPresent()) {
            Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                // Cancel all of the slot transactions as we don't want the default behavior
                for (SlotTransaction slotTransaction : event.getTransactions()) {
                    slotTransaction.setValid(false);
                }
            }
        }
    }

    @Listener
    public void onShiftClickInventory(ClickInventoryEvent.Shift event, @First Player player, @Getter("getTargetInventory") Inventory inventory) {
        Optional<TileEntity> tileEntityOpt = getTileEntityFromPlayerRaycast(player);

        if (tileEntityOpt.isPresent()) {
            Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                Shop shop = shopOpt.get();
                ItemStack clickedItem = ItemStack.builder().fromSnapshot(event.getTransactions().get(0).getOriginal()).build();
                Optional<ShopItem> shopItemOpt = clickedItem.get(ShopKeys.SHOP_ITEM);

                if (player.getUniqueId().equals(shop.getOwner()) && shopItemOpt.isPresent()) {
                    ShopItem shopItem = shopItemOpt.get();

                    for (SlotTransaction transaction : event.getTransactions()) {
                        transaction.setCustom(ItemStack.empty());
                    }

                    removeShopItemData(clickedItem);

                    ItemStack returnedItem = clickedItem.copy();
                    returnedItem.setQuantity(shopItem.getQuantity());

                    player.getInventory().offer(returnedItem);

                    // Set the stock of the shop to that of the open inventory
                    shop.setStock(getShopStockFromInventory(inventory));
                    tileEntityOpt.get().offer(new ShopData(shop));
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onInventoryNumberPress(ClickInventoryEvent.NumberPress event, @First Player player) {
        Optional<TileEntity> tileEntityOpt = getTileEntityFromPlayerRaycast(player);

        if (tileEntityOpt.isPresent()) {
            Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onInventoryOpen(InteractInventoryEvent.Open event, @First Player player) {
        Optional<BlockSnapshot> blockSnapshotOpt = event.getCause().get("HitTarget", BlockSnapshot.class);

        if (blockSnapshotOpt.isPresent()) {
            BlockSnapshot blockSnapshot = blockSnapshotOpt.get();
            Optional<Shop> shopOpt = blockSnapshot.get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                Shop shop = shopOpt.get();

                Inventory shopInventory = Inventory.builder().of(InventoryArchetypes.CHEST)
                        .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(TextStyles.BOLD, TextColors.BLUE, shop.getTitle())))
                        .build(totalEconomy.getPluginContainer());

                int counter = 0;
                for (Inventory slot : shopInventory.slots()) {
                    if (counter >= shop.getStock().size()) {
                        break;
                    }

                    slot.set(shop.getStock().get(counter));

                    counter++;
                }

                // Using a custom inventory as they are a lot easier to work with for this
                player.openInventory(shopInventory, Cause.of(NamedCause.source(totalEconomy.getPluginContainer())));
            }
        }
    }

    @Listener
    public void onShopDestroy(ChangeBlockEvent.Break event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTransactions().get(0).getOriginal();
        Optional<Shop> shopOpt = blockSnapshot.get(ShopKeys.SINGLE_SHOP);

        if (shopOpt.isPresent()) {
            Shop shop = shopOpt.get();
            UUID shopOwner = shop.getOwner();
            List<ItemStack> shopStock = shop.getStock();

            if (!player.getUniqueId().equals(shopOwner)) {
                event.setCancelled(true);

                player.sendMessage(messageManager.getMessage("shops.remove.notowner"));
            } else if (player.getUniqueId().equals(shopOwner) && !shopStock.isEmpty()) {
                event.setCancelled(true);

                player.sendMessage(messageManager.getMessage("shops.remove.stocked"));
            } else {
                player.sendMessage(messageManager.getMessage("shops.remove.success"));
            }
        }
    }

    @Listener
    public void onChestPlace(ChangeBlockEvent.Place event) {
        BlockSnapshot blockSnapshot = event.getTransactions().get(0).getDefault();
        BlockType blockType = blockSnapshot.getState().getType();
        Location location = blockSnapshot.getLocation().get();

        if (blockType.equals(BlockTypes.CHEST) && isPlacedNextToShop(location)) {
            event.setCancelled(true);
        }
    }

    private void removeShopItemData(ItemStack itemStack) {
        itemStack.remove(Keys.ITEM_LORE);
        itemStack.remove(ShopKeys.SHOP_ITEM);
    }

    private boolean isPlacedNextToShop(Location location) {
        boolean isPlacedNextToShop = false;

        Optional<TileEntity> northTileEntity = location.getBlockRelative(Direction.NORTH).getTileEntity();
        Optional<TileEntity> eastTileEntity = location.getBlockRelative(Direction.EAST).getTileEntity();
        Optional<TileEntity> southTileEntity = location.getBlockRelative(Direction.SOUTH).getTileEntity();
        Optional<TileEntity> westTileEntity = location.getBlockRelative(Direction.WEST).getTileEntity();

        if (northTileEntity.isPresent()) {
            isPlacedNextToShop = northTileEntity.get().get(ShopKeys.SINGLE_SHOP).isPresent();
        }

        if (eastTileEntity.isPresent()) {
            isPlacedNextToShop = eastTileEntity.get().get(ShopKeys.SINGLE_SHOP).isPresent();
        }

        if (southTileEntity.isPresent()) {
            isPlacedNextToShop = southTileEntity.get().get(ShopKeys.SINGLE_SHOP).isPresent();
        }

        if (westTileEntity.isPresent()) {
            isPlacedNextToShop = westTileEntity.get().get(ShopKeys.SINGLE_SHOP).isPresent();
        }

        return isPlacedNextToShop;
    }

    private List<ItemStack> getShopStockFromInventory(Inventory inventory) {
        List<ItemStack> stock = new ArrayList<>();

        int upperSize = inventory.iterator().next().capacity();
        for (Inventory slot : inventory.slots()) {
            int affectedSlot = slot.getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
            boolean upperInventory = affectedSlot != -1 && affectedSlot < upperSize;

            if (upperInventory) {
                if (slot.peek().isPresent()) {
                    stock.add(slot.peek().get());
                }
            }
        }

        return stock;
    }

    private void updateItemInSlot(ShopItem shopItem, Slot slot, ItemStack slotItem) {
        if (shopItem.getQuantity() <= 0) {
            clearSlot(slot);
        } else {
            slotItem.offer(new ShopItemData(shopItem));
            slotItem.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
            slot.set(slotItem);
        }
    }

    private void clearSlot(Slot slot) {
        slot.set(ItemStack.empty());
    }

    public Optional<TileEntity> getTileEntityFromPlayerRaycast(Player player) {
        Optional<BlockRayHit<World>> optHit = BlockRay.from(player).skipFilter(BlockRay.blockTypeFilter(BlockTypes.CHEST)).distanceLimit(3).build().end();

        if (optHit.isPresent()) {
            return optHit.get().getLocation().getTileEntity();
        }

        return Optional.empty();
    }

    public Optional<BlockSnapshot> getBlockSnapshotFromPlayerRaycast(Player player) {
        Optional<BlockRayHit<World>> optHit = BlockRay.from(player).skipFilter(BlockRay.blockTypeFilter(BlockTypes.CHEST)).distanceLimit(3).build().end();

        if (optHit.isPresent()) {
            return Optional.of(optHit.get().getLocation().createSnapshot());
        }

        return Optional.empty();
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getChestShopPrice() {
        return chestShopPrice;
    }
}
