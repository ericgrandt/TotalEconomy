package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.event.Event;

import java.util.Optional;

public abstract class AbstractBlockAction<T extends Event> extends AbstractTEAction<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractBlockAction.class);

    private final String idTrait;
    private final String idTraitValue;

    public AbstractBlockAction(TEActionReward baseReward) {
        super(baseReward);
        this.idTrait = null;
        this.idTraitValue = null;
    }

    public AbstractBlockAction(TEActionReward baseReward, String idTrait, String idTraitValue) {
        super(baseReward);
        this.idTrait = idTrait;
        this.idTraitValue = idTraitValue;
    }

    /**
     * Checks whether a given block state contains a block of the matching trait identification.
     * If no trait for identification is given, returns true.
     */
    protected boolean checkIdTrait(BlockState blockState) {
        if (idTrait == null || idTrait.isEmpty()) {
            return true;
        }

        String blockId = blockState.getId();
        Optional<BlockTrait<?>> trait = blockState.getTrait(idTrait);

        if (trait.isPresent()) {
            Optional<?> traitVal = blockState.getTraitValue(trait.get());

            // If the trait value could not be found, log a warning.
            if (!traitVal.isPresent()) {
                logger.warn("ID trait \"{}\" has no stored value. Block: {}", idTrait, blockId);
                return false;
            }

            // If the value does not match, this is not the block we are looking for.
            return traitVal.get().equals(idTraitValue);
        } else {
            logger.warn("ID trait \"{}\" not found. Block: {}", idTrait, blockId);
            return false;
        }
    }
}
