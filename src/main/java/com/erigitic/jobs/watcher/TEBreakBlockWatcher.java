package com.erigitic.jobs.watcher;

import com.erigitic.jobs.TEActionReward;
import com.erigitic.jobs.TEJobSet;
import com.erigitic.jobs.actions.TEBreakAction;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class TEBreakBlockWatcher extends AbstractWatcher {

    private static final Logger logger = LoggerFactory.getLogger(TEBreakBlockWatcher.class);

    public TEBreakBlockWatcher(TotalEconomy totalEconomy) {
        super(totalEconomy);
    }

    @Listener
    public void onPlayerBreakBlock(ChangeBlockEvent.Break event) {
        final Optional<Player> optPlayerCause = event.getCause().first(Player.class);
        if (optPlayerCause.isPresent()) {
            Player player = optPlayerCause.get();
            final List<TEJobSet> applicableSets = getJobManager().getSetsApplicableTo(player);

            // Iterate over all transactions so breaking multiple blocks will yield correct rewards.
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                final Optional<TEActionReward> optReward = processTransaction(transaction, applicableSets, player);

                //noinspection OptionalIsPresent
                if (optReward.isPresent()) {
                    payReward(optReward.get(), player, event.getCause());
                }
            }
        }
    }

    private Optional<TEActionReward> processTransaction(Transaction<BlockSnapshot> transaction, List<TEJobSet> sets, Player player) {
        final BlockState blockState = transaction.getOriginal().getState();
        final BlockType blockType = blockState.getType();
        final String blockId = blockType.getId();
        final String blockName = blockType.getName();
        checkDebugNotification(player, blockState, blockType);

        // Calculate the largest applicable reward (money-wise)
        final TEActionReward[] largestReward = {null};
        for (TEJobSet set : sets) {
            final Optional<TEBreakAction> optAction = set.getBreakAction(blockId, blockName);

            // For all sets, having a break action for this blockId or blockName, check the rewards.
            // Always keep the largest reward found.
            optAction.map(action -> {
                if (action.checkTransaction(transaction)) {
                    return action.getRewardFor(blockState).orElse(null);
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

    private void checkDebugNotification(Player player, BlockState state, BlockType blockType) {
        // Enable admins to determine block information by displaying it to them - WHEN they have the flag enabled
        if (getAccountManager().getUserOption("totaleconomy:block-break-info", player).orElse("0").equals("1")) {
            List<BlockTrait<?>> traits = new ArrayList<>(state.getTraits());
            int count = traits.size();
            List<Text> traitTexts = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                Object traitValue = state.getTraitValue(traits.get(i)).orElse(null);
                traitTexts.add(i, Text.of(traits.get(i).getName(), '=', traitValue != null ? traitValue.toString() : "null"));
            }

            Text t = Text.of(TextColors.GRAY, "TRAITS:\n\t", Text.joinWith(Text.of(",\n\t"), traitTexts.toArray(new Text[0])));
            player.sendMessage(Text.of("Block-Name: ", blockType.getName()));
            player.sendMessage(Text.of("Block-Id: ", blockType.getId()));
            player.sendMessage(t);
        }
    }
}
