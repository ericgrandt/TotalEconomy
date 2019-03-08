package com.erigitic.jobs.watcher;

import com.erigitic.jobs.TEActionReward;
import com.erigitic.jobs.TEJobSet;
import com.erigitic.jobs.actions.TEFishAction;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class TEFishWatcher extends AbstractWatcher {

    public TEFishWatcher(TotalEconomy totalEconomy) {
        super(totalEconomy);
    }

    /**
     * Evaluates a reward for a player fishing an item.
     * Suppressed duplicates: Member {@link FishingEvent.Stop#getTransactions()} has no parent with {@link ChangeBlockEvent#getTransactions()}
     */
    @SuppressWarnings("Duplicates")
    @Listener
    public void onPlayerFish(FishingEvent.Stop event) {
        final Optional<Player> optPlayerCause = event.getCause().first(Player.class);
        if (optPlayerCause.isPresent()) {
            Player player = optPlayerCause.get();
            final List<TEJobSet> applicableSets = getJobManager().getSetsApplicableTo(player);

            // Iterate over all transactions so breaking multiple blocks will yield correct rewards.
            for (Transaction<ItemStackSnapshot> transaction : event.getTransactions()) {
                final Optional<TEActionReward> optReward = processTransaction(transaction, applicableSets, player);

                //noinspection OptionalIsPresent
                if (optReward.isPresent()) {
                    payReward(optReward.get(), player, event.getCause());
                }
            }
        }
    }

    private Optional<TEActionReward> processTransaction(Transaction<ItemStackSnapshot> transaction, List<TEJobSet> sets, Player player) {
        final ItemType itemType = transaction.getFinal().getType();
        final String itemId = itemType.getId();
        final String itemName = itemType.getName();
        checkDebugNotification(player, itemType);

        // Calculate the largest applicable reward (money-wise)
        final TEActionReward[] largestReward = {null};
        for (TEJobSet set : sets) {
            final Optional<TEFishAction> optAction = set.getFishAction(itemId, itemName);

            // For all sets, having a break action for this itemId or itemName, check the rewards.
            // Always keep the largest reward found.
            optAction.map(action -> {
                if (action.checkTransaction(transaction)) {
                    return action.getRewardFor(null).orElse(null);
                } else {
                    return null;
                }

            }).ifPresent(reward -> {
                if (largestReward[0] == null
                        || largestReward[0].getMoneyReward() < reward.getMoneyReward()) {
                    largestReward[0] = reward;
                }
            });
        }

        return Optional.ofNullable(largestReward[0]);
    }

    private void checkDebugNotification(Player player, ItemType itemType) {
        // Enable admins to determine fish information by displaying it to them - WHEN they have the flag enabled
        if (getAccountManager().getUserOption("totaleconomy:entity-fish-info", player).orElse("0").equals("1")) {
            player.sendMessage(Text.of("Fish-Name: ", itemType.getName()));
            player.sendMessage(Text.of("Fish-ID: ", itemType.getId()));
        }
    }
}
