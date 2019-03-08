package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

/**
 * An action that rewards a player for breaking a specific block.
 * Break actions allow to decide between block types by ID and a block trait.
 * Break actions can lower the reward for not fully grown block that indicate growth using a numerical block trait.
 */
public class TEBreakAction extends AbstractBlockAction<ChangeBlockEvent.Break> {

    private static final Logger logger = LoggerFactory.getLogger(TEBreakAction.class);

    private String growthTrait = null;

    public TEBreakAction(TEActionReward baseReward) {
        super(baseReward);
    }

    public TEBreakAction(TEActionReward baseReward, String growthTrait, String idTrait, String idTraitTargetValue) {
        super(baseReward, idTrait, idTraitTargetValue);
        this.growthTrait = growthTrait;
    }

    private boolean hasGrowthTrait() {
        return growthTrait != null && !growthTrait.isEmpty();
    }

    @Override
    public Optional<TEActionReward> evaluate(ChangeBlockEvent.Break triggerEvent) {
        double[] moneyReward = new double[]{0d};
        int[] expReward = new int[]{0};

        // Aggregate rewards over all transactions in case the player broke multiple blocks
        for (Transaction<BlockSnapshot> transaction : triggerEvent.getTransactions()) {
            final Optional<UUID> optCreator = transaction.getOriginal().getCreator();
            final BlockState finalBlockState = transaction.getFinal().getState();

            // For now: If the block was placed by a player and does not have any growth trait, pay no reward.
            // ==> If the block has no creator OR grows, add aggregate the reward
            if (!optCreator.isPresent() || hasGrowthTrait()) {
                aggregateReward(finalBlockState).ifPresent(reward -> {
                    moneyReward[0] += reward.getMoneyReward();
                    expReward[0] += reward.getExpReward();
                });
            }
        }

        // If any reward has been collected, construct a single reward for the aggregated result.
        if (moneyReward[0] == 0 && expReward[0] == 0) {
            return Optional.empty();
        } else {
            return Optional.of(makeReward(moneyReward[0], expReward[0]));
        }
    }

    private Optional<TEActionReward> aggregateReward(BlockState blockState) {
        // If the action has an ID-trait configured, we need to further validate the blocks identity.
        final String blockId = blockState.getType().getId();
        if (!checkIdTrait(blockState)) {
            return Optional.empty();

        } else if (hasGrowthTrait()) {
            return calculateGrowthReward(blockState, blockId);
        } else {
            return Optional.of(getBaseReward());
        }
    }

    @SuppressWarnings("unchecked")
    private Optional<TEActionReward> calculateGrowthReward(BlockState blockState, String blockId) {
        Optional<BlockTrait<?>> trait = blockState.getTrait(growthTrait);
        if (!trait.isPresent()) {
            logger.warn("Growth trait \"{}\" not found. Break: {}", growthTrait, blockId);
            return Optional.empty();
        }

        if (!Integer.class.isAssignableFrom(trait.get().getValueClass())) {
            logger.warn("Growth trait \"{}\" is not numeric! Break: {}", growthTrait, blockId);
            return Optional.empty();
        }

        Optional<?> traitVal = blockState.getTraitValue(trait.get());
        if (!traitVal.isPresent()) {
            logger.warn("Growth trait \"{}\" has missing value. Break: {}", growthTrait, blockId);
            return Optional.empty();
        }

        final Integer traitValue = (Integer) traitVal.get();
        final Collection<Integer> possibleValues = (Collection<Integer>) trait.get().getPossibleValues();
        return Optional.of(generateReducibleReward(traitValue, possibleValues));
    }

    private TEActionReward generateReducibleReward(Integer traitValue, Collection<Integer> possibleValues) {
        int max = possibleValues.stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0);
        int min = possibleValues.stream().min(Comparator.comparingInt(Integer::intValue)).orElse(0);
        final double diff = (max - min);

        if (diff != 0) {
            double percent = (double) (traitValue - min) / diff;
            int expReward = (int) (getBaseReward().getExpReward() * percent);
            double moneyReward = getBaseReward().getMoneyReward() * percent;

            TEActionReward partialReward = new TEActionReward();
            partialReward.setValues(expReward, moneyReward, getBaseReward().getCurrencyId());
            return partialReward;

        } else {
            // Difference is only 0 when max and min are the same.
            // We will just say that's 100% grown.
            return getBaseReward();
        }
    }
}
