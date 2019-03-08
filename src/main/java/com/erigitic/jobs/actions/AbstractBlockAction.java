package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractBlockAction<T extends ChangeBlockEvent> extends AbstractTEAction<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBlockAction.class);

    private final String blockId;
    private final String idTrait;

    private final Map<String, TEActionReward> baseRewardsByTrait = new HashMap<>();

    public AbstractBlockAction(TEActionReward baseReward, String blockId) {
        super(baseReward);
        this.blockId = blockId;
        this.idTrait = null;
    }

    public AbstractBlockAction(TEActionReward baseReward, String blockId, String idTrait) {
        super(baseReward);
        this.blockId = blockId;
        this.idTrait = idTrait;
    }

    public void addRewardForTraitValue(String traitValue, TEActionReward baseReward) {
        baseRewardsByTrait.put(traitValue, baseReward);
    }

    @Override
    public Optional<TEActionReward> evaluate(T changeBlockEvent) {
        double[] moneyReward = new double[]{0d};
        int[] expReward = new int[]{0};

        // Aggregate rewards over all transactions in case the player broke multiple blocks
        for (Transaction<BlockSnapshot> transaction : changeBlockEvent.getTransactions()) {
            if (checkTransaction(transaction)) {
                final BlockState blockState = getBlockState(transaction);
                final BlockType blockType = blockState.getType();

                if (blockType.getId().equalsIgnoreCase(blockId) || blockType.getName().equalsIgnoreCase(blockId)) {
                    aggregateReward(blockState).ifPresent(reward -> {
                        moneyReward[0] += reward.getMoneyReward();
                        expReward[0] += reward.getExpReward();
                    });
                }
            }
        }

        // If any reward has been collected, construct a single reward for the aggregated result.
        if (moneyReward[0] == 0 && expReward[0] == 0) {
            return Optional.empty();
        } else {
            return Optional.of(makeReward(moneyReward[0], expReward[0]));
        }
    }

    /**
     * Returns the configured reward based on the block traits configured and the given block with its traits.
     * If no trait is configured for identification, the base reward is used.
     */
    protected Optional<TEActionReward> getTraitedReward(BlockState blockState) {
        if (idTrait == null || idTrait.isEmpty()) {
            return Optional.of(getBaseReward());
        }

        String blockId = blockState.getId();
        Optional<BlockTrait<?>> trait = blockState.getTrait(idTrait);

        if (trait.isPresent()) {
            Optional<?> traitVal = blockState.getTraitValue(trait.get());

            // If the trait value could not be found, log a warning.
            if (!traitVal.isPresent()) {
                logger.warn("ID trait \"{}\" has no stored value. Block: {}", idTrait, blockId);
                return Optional.empty();
            }

            // If the value does not match, this is not the block we are looking for.
            final Object traitValue = traitVal.get();
            return Optional.ofNullable(baseRewardsByTrait.getOrDefault(traitValue, null));

        } else {
            logger.warn("ID trait \"{}\" not found. Block: {}", idTrait, blockId);
            return Optional.empty();
        }
    }

    /**
     * Whether or not this block transaction should be taken into consideration
     * while iterating over transactions.
     */
    protected abstract boolean checkTransaction(Transaction<BlockSnapshot> transaction);

    /**
     * Return the block state to consider from the transaction.
     * (original vs. final block state)
     */
    protected abstract BlockState getBlockState(Transaction<BlockSnapshot> transaction);

    /**
     * Calculate the reward for this particular block state from {@link #getBlockState(Transaction)}.
     */
    protected abstract Optional<TEActionReward> aggregateReward(BlockState blockState);
}
