package com.erigitic.shops;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.data.ShopData;
import com.erigitic.shops.data.ShopItemData;
import com.erigitic.shops.data.ShopKeys;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.*;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopManager {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Logger logger;

    public ShopManager(TotalEconomy totalEconomy, AccountManager accountManager, Logger logger) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.logger = logger;
    }

    /**
     * Handles a user left clicking (purchasing) an item from a shop.
     *
     * @param event
     * @param player The player clicking within an inventory
     * @param inventory The inventory being clicked
     */
    // TODO: Put some of this code in a seperate function as it will be reused in the ClickInventoryEvent.Secondary function
    @Listener
    public void onItemPurchase(ClickInventoryEvent.Primary event, @First Player player, @Getter("getTargetInventory") Inventory inventory) {
        Slot clickedSlot = event.getTransactions().get(0).getSlot();
        ItemStack itemStack = ItemStack.builder().fromSnapshot(event.getCursorTransaction().getDefault()).build();

        if (itemStack.get(ShopKeys.SHOP_ITEM).isPresent()) {
            event.getCursorTransaction().setValid(false);

            ShopItem shopItem = itemStack.get(ShopKeys.SHOP_ITEM).get();

            player.getInventory().offer(ItemStack.of(itemStack.getItem(), 1));

            shopItem.setQuantity(shopItem.getQuantity() - 1);

            if (shopItem.getQuantity() <= 0) {
                clickedSlot.set(ItemStack.empty());
            } else {
                itemStack.offer(new ShopItemData(shopItem));
                itemStack.offer(Keys.ITEM_LORE, shopItem.getLore(totalEconomy.getDefaultCurrency()));
                clickedSlot.set(itemStack);
            }

            List<ItemStack> stock = new ArrayList<>();

            int upperSize = event.getTargetInventory().iterator().next().capacity();
            for (Inventory slot : inventory.slots()) {
                Integer affectedSlot = slot.getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
                boolean upperInventory = affectedSlot != -1 && affectedSlot < upperSize;

                if (upperInventory) {
                    if (slot.peek().isPresent()) {
                        stock.add(slot.peek().get());
                    }
                }
            }

            Optional<BlockRayHit<World>> optHit = BlockRay.from(player).skipFilter(BlockRay.blockTypeFilter(BlockTypes.CHEST)).distanceLimit(3).build().end();

            if (optHit.isPresent()) {
                BlockRayHit<World> hitBlock = optHit.get();
                Optional<TileEntity> tileEntityOpt = hitBlock.getLocation().getTileEntity();

                if (tileEntityOpt.isPresent()) {
                    Optional<Shop> shopOpt = tileEntityOpt.get().get(ShopKeys.SINGLE_SHOP);

                    if (shopOpt.isPresent()) {
                        Shop shop = shopOpt.get();

                        shop.setStock(stock);

                        tileEntityOpt.get().offer(new ShopData(shop));
                    }
                }
            }
        }
    }

    @Listener
    public void onShiftClickInventory(ClickInventoryEvent.Shift event) {
        // TODO: This may still work in the above event. Check if the transaction occurs, if so, cancel event.
        ItemStack itemStack = ItemStack.builder().fromSnapshot(event.getTransactions().get(0).getOriginal()).build();

        if (itemStack.get(ShopKeys.SHOP_ITEM).isPresent()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInventoryOpen(InteractInventoryEvent.Open event, @First Player player) {
        Optional<BlockSnapshot> blockSnapshotOpt = event.getCause().get("HitTarget", BlockSnapshot.class);

        if (blockSnapshotOpt.isPresent()) {
            BlockSnapshot blockSnapshot = blockSnapshotOpt.get();
            Optional<TileEntity> tileEntityOpt = blockSnapshot.getLocation().get().getTileEntity();

            if (tileEntityOpt.isPresent()) {
                TileEntity tileEntity = tileEntityOpt.get();
                Optional<Shop> shopOpt = tileEntity.get(ShopKeys.SINGLE_SHOP);

                if (shopOpt.isPresent()) {
                    Shop shop = tileEntity.get(ShopKeys.SINGLE_SHOP).get();

                    Inventory shopInventory = Inventory.builder().of(InventoryArchetypes.CHEST)
                            .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of(TextColors.GOLD, shop.getTitle())))
                            .build(totalEconomy.getPluginContainer());

                    int counter = 0;

                    for (Inventory slot : shopInventory.slots()) {
                        if (counter >= shop.getStock().size()) break;

                        slot.set(shop.getStock().get(counter));

                        counter++;
                    }

                    // Using a custom inventory as they are a lot easier to work with for this
                    player.openInventory(shopInventory, Cause.of(NamedCause.source(totalEconomy.getPluginContainer())));
                }
            }
        }
    }
}
