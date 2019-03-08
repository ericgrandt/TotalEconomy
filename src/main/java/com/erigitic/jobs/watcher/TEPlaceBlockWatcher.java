package com.erigitic.jobs.watcher;

import com.erigitic.main.TotalEconomy;
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

public class TEPlaceBlockWatcher extends AbstractBlockChangeWatcher<ChangeBlockEvent.Place> {

    public TEPlaceBlockWatcher(TotalEconomy totalEconomy) {
        super(totalEconomy);
    }

    @Listener
    public void onPlayerBreakBlock(ChangeBlockEvent.Place event) {
        onChangeBlockEvent(event);
    }

    @Override
    protected BlockState getState(Transaction<BlockSnapshot> transaction) {
        return transaction.getFinal().getState();
    }

    @Override
    protected void checkDebugNotification(Player player, BlockState state, BlockType blockType) {
        // Enable admins to determine block information by displaying it to them - WHEN they have the flag enabled
        if (getAccountManager().getUserOption("totaleconomy:block-place-info", player).orElse("0").equals("1")) {
            sendBlockDebugInfo(player, state, blockType);
        }
    }

}
