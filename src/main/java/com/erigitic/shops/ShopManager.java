package com.erigitic.shops;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.data.ShopData;
import com.erigitic.shops.data.ShopKeys;
import org.slf4j.Logger;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;

public class ShopManager {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Logger logger;

    public ShopManager(TotalEconomy totalEconomy, AccountManager accountManager, Logger logger) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.logger = logger;
    }

    @Listener
    public void onInventoryClick(ClickInventoryEvent event, @First Player player) {
        Inventory inventory = event.getTargetInventory();
        String itemNameOriginal = event.getCursorTransaction().getOriginal().getType().getName(); // On drop
        String itemNameDefault = event.getCursorTransaction().getDefault().getType().getName(); // On pickup
        String itemNameFinal = event.getCursorTransaction().getFinal().getType().getName(); // On pickup

        int upperSize = inventory.iterator().next().capacity();

        for (SlotTransaction transaction : event.getTransactions()) {
            int affectedSlot = transaction.getSlot().getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
            boolean upperInventory = affectedSlot != -1 && affectedSlot < upperSize;

            logger.info("" + upperInventory);
        }

        ItemStack chestShop = ItemStack.builder().itemType(ItemTypes.CHEST).build();

        SingleItemShop shop = new SingleItemShop(player.getUniqueId(), "My Shop", ItemStack.of(ItemTypes.BEDROCK, 1));
        ShopData shopData = new ShopData(shop);

        chestShop.offer(shopData);
        chestShop.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, shop.getTitle()));

        List<Text> shopLore = new ArrayList<>();
        shopLore.add(Text.of(TextColors.GRAY, shop.getOwner()));

        chestShop.offer(Keys.ITEM_LORE, shopLore);

        logger.info(chestShop.get(ShopKeys.SINGLE_SHOP).get().getTitle());

        player.getInventory().offer(chestShop);

        // TODO: When a command is run they will be given a chest with some special lore. They can create items for sale by holding
        // TODO: it in their hand and running a command to set the sell/buy price.
    }

}
