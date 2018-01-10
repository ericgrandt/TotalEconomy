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

package com.erigitic.shops;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.data.PlayerShopInfoData;
import com.erigitic.shops.data.ShopItemData;
import com.erigitic.shops.data.ShopKeys;
import com.erigitic.util.MessageManager;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.*;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
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
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
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
    private final int maxStackSize;

    public ShopManager(TotalEconomy totalEconomy, AccountManager accountManager, MessageManager messageManager) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.messageManager = messageManager;

        minPrice = this.totalEconomy.getShopNode().getNode("min-item-price").getDouble(0);
        maxPrice = this.totalEconomy.getShopNode().getNode("max-item-price").getDouble(1000000000);
        chestShopPrice = this.totalEconomy.getShopNode().getNode("chestshop", "price").getDouble(1000);
        maxStackSize = this.totalEconomy.getShopNode().getNode("chestshop", "max-stack").getInt(64);
    }

    /**
     * Handles a user purchasing an item from a shop.
     *
     * @param event Primary (Left mouse) click inventory
     * @param player The player clicking within an inventory
     * @param inventory The inventory being interacted with
     */
    @Listener
    @Exclude(ClickInventoryEvent.Shift.class)
    public void onItemPurchase(ClickInventoryEvent.Primary event, @First Player player, @Getter("getTargetInventory") Inventory inventory) {
        Optional<PlayerShopInfo> playerShopInfoOpt = player.get(ShopKeys.PLAYER_SHOP_INFO);

        if (playerShopInfoOpt.isPresent()) {
            Location location = playerShopInfoOpt.get().getOpenShopLocation();
            Optional<TileEntity> tileEntityOpt = location.getTileEntity();

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
                            ItemStack purchasedItem = removeShopItemData(clickedItem.copy());

                            Collection<ItemStackSnapshot> rejectedItems = player.getInventory().query(GridInventory.class, Hotbar.class).offer(purchasedItem).getRejectedItems();

                            if (rejectedItems.size() == 0) {
                                customerAccount.transfer(ownerAccount, totalEconomy.getDefaultCurrency(), BigDecimal.valueOf(shopItem.getPrice()), Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                                shopItem.setQuantity(shopItem.getQuantity() - 1);

                                if (shopItem.getQuantity() <= 0) {
                                    shop.setCapacity(shop.getCapacity() - 1);
                                }

                                updateItemInSlot(shopItem, clickedSlot, clickedItem);
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
    }

    /**
     * Prevents right clicking items within a shop.
     *
     * @param event Secondary (Right mouse) click inventory
     * @param player The player clicking within an inventory
     */
    @Listener
    public void onShopSecondaryClick(ClickInventoryEvent.Secondary event, @First Player player) {
        Optional<PlayerShopInfo> playerShopInfoOpt = player.get(ShopKeys.PLAYER_SHOP_INFO);

        if (playerShopInfoOpt.isPresent()) {
            Location location = player.get(ShopKeys.PLAYER_SHOP_INFO).get().getOpenShopLocation();
            Optional<TileEntity> tileEntityOpt = location.getTileEntity();

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
    }

    /**
     * Handles removing items from a shop. Items will be removed if the shop owner shift clicks them, otherwise
     * the event will be canceled.
     *
     * @param event Shift click inventory
     * @param player The player shift clicking within an inventory
     * @param inventory The inventory being interacted with
     */
    @Listener
    public void onShiftClickInventory(ClickInventoryEvent.Shift event, @First Player player, @Getter("getTargetInventory") Inventory inventory) {
        Optional<PlayerShopInfo> playerShopInfoOpt = player.get(ShopKeys.PLAYER_SHOP_INFO);

        if (playerShopInfoOpt.isPresent()) {
            Location location = player.get(ShopKeys.PLAYER_SHOP_INFO).get().getOpenShopLocation();
            Optional<TileEntity> tileEntityOpt = location.getTileEntity();

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

                        ItemStack returnedItem = removeShopItemData(clickedItem.copy());
                        returnedItem.setQuantity(shopItem.getQuantity());

                        shop.setCapacity(shop.getCapacity() - 1);

                        player.getInventory().offer(returnedItem);
                    } else if (player.getUniqueId().equals(shop.getOwner())) {
                        event.setCancelled(false);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * Prevents shop items from being swapped to hotbar slots when number keys are pressed.
     *
     * @param event Number press within inventory
     * @param player The player interacting with the inventory
     */
    @Listener
    public void onInventoryNumberPress(ClickInventoryEvent.NumberPress event, @First Player player) {
        Optional<PlayerShopInfo> playerShopInfoOpt = player.get(ShopKeys.PLAYER_SHOP_INFO);

        if (playerShopInfoOpt.isPresent()) {
            Location location = player.get(ShopKeys.PLAYER_SHOP_INFO).get().getOpenShopLocation();
            Optional<TileEntity> tileEntityOpt = location.getTileEntity();

            if (tileEntityOpt.isPresent()) {
                Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

                if (shopOpt.isPresent()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Handles the creation and opening of a chest shop's inventory.
     *
     * @param event Open inventory
     * @param player The player opening the inventory
     */
    @Listener
    public void onInventoryOpen(InteractInventoryEvent.Open event, @First Player player) {
        Optional<BlockSnapshot> blockSnapshotOpt = event.getCause().get("HitTarget", BlockSnapshot.class);

        if (blockSnapshotOpt.isPresent()) {
            BlockSnapshot blockSnapshot = blockSnapshotOpt.get();
            Optional<Shop> shopOpt = blockSnapshot.get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                player.offer(new PlayerShopInfoData(new PlayerShopInfo(blockSnapshot.getLocation().get())));
            }
        }
    }

    /**
     * Removes PlayerShopInfoData from a player when an inventory is closed.
     *
     * @param event Close inventory
     * @param player The player who closed the inventory
     */
    @Listener
    public void onInventoryClose(InteractInventoryEvent.Close event, @First Player player) {
        Optional<PlayerShopInfo> playerShopInfoOpt = player.get(ShopKeys.PLAYER_SHOP_INFO);

        if (playerShopInfoOpt.isPresent()) {
            player.remove(ShopKeys.PLAYER_SHOP_INFO);
        }
    }

    /**
     * Handles a chest shop being destroyed.
     *
     * @param event Pre break block
     * @param player The player who broke the block
     */
    @Listener
    public void onShopDestroy(ChangeBlockEvent.Break.Pre event, @First Player player) {
        Optional<TileEntity> tileEntityOpt = event.getLocations().get(0).getTileEntity();

        if (tileEntityOpt.isPresent()) {
            TileEntity tileEntity = tileEntityOpt.get();
            Optional<Shop> shopOpt = tileEntity.get(ShopKeys.SINGLE_SHOP);

            if (shopOpt.isPresent()) {
                Shop shop = shopOpt.get();
                UUID shopOwner = shop.getOwner();
                int shopCapacity = shop.getCapacity();

                if (!player.getUniqueId().equals(shopOwner)) {
                    event.setCancelled(true);

                    player.sendMessage(messageManager.getMessage("shops.remove.notowner"));
                } else if (player.getUniqueId().equals(shopOwner) && shopCapacity > 0) {
                    event.setCancelled(true);

                    player.sendMessage(messageManager.getMessage("shops.remove.stocked"));
                } else {
                    event.getLocations().get(0).removeBlock(Cause.of(NamedCause.of("totaleconomy", totalEconomy.getPluginContainer())));
                    event.getLocations().get(0).setBlockType(BlockTypes.CHEST, Cause.of(NamedCause.of("totaleconomy", totalEconomy.getPluginContainer())));
                }
            }
        }
    }

    /**
     * Prevents chests from being placed next to chest shops.
     *
     * @param event Place block
     */
    @Listener
    public void onChestPlace(ChangeBlockEvent.Place event) {
        BlockSnapshot blockSnapshot = event.getTransactions().get(0).getDefault();
        BlockType blockType = blockSnapshot.getState().getType();
        Location location = blockSnapshot.getLocation().get();

        if (blockType.equals(BlockTypes.CHEST) && isPlacedNextToShop(location)) {
            event.setCancelled(true);
        }
    }

    /**
     * Removes ShopItemData from an ItemStack.
     *
     * @param itemStack The ItemStack to remove ShopItemData from
     * @return ItemStack An ItemStack with ShopItemData removed
     */
    private ItemStack removeShopItemData(ItemStack itemStack) {
        itemStack.remove(Keys.ITEM_LORE);
        itemStack.remove(ShopKeys.SHOP_ITEM);

        // Remove the DataQuery for lore, otherwise an empty NBT tag will be attached to the item
        DataContainer dataContainer = itemStack.toContainer().remove(DataQuery.of("DefaultReplacement", "UnsafeData", "display", "Lore")).copy();
        itemStack = ItemStack.builder().fromContainer(dataContainer).itemType(itemStack.getItem()).build();

        return itemStack;
    }

    /**
     * Checks if a location is adjacent to a chest shop.
     *
     * @param location The location to check for adjacent chest shops
     * @return boolean If the location is adjacent to a chest shop
     */
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

    /**
     * Updates a shop item within a slot.
     *
     * @param shopItem The ShopItem in the slot
     * @param slot The slot to update
     * @param slotItem The item in the slot
     */
    private void updateItemInSlot(ShopItem shopItem, Slot slot, ItemStack slotItem) {
        if (shopItem.getQuantity() <= 0) {
            clearSlot(slot);
        } else {
            slotItem.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
            slotItem.offer(new ShopItemData(shopItem));
            slot.set(slotItem);
        }
    }

    private void clearSlot(Slot slot) {
        slot.set(ItemStack.empty());
    }

    /**
     * Gets a TileEntity from a chest that a player is looking at.
     *
     * @param player The player to get a raycast from
     * @return Optional The tile entity the player is looking at
     */
    public Optional<TileEntity> getTileEntityFromPlayerRaycast(Player player) {
        Optional<BlockRayHit<World>> optHit = BlockRay.from(player).skipFilter(BlockRay.blockTypeFilter(BlockTypes.CHEST)).distanceLimit(3).build().end();

        if (optHit.isPresent()) {
            return optHit.get().getLocation().getTileEntity();
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

    public int getMaxStackSize() {
        return maxStackSize;
    }
}
