package de.aska.game.event;

import de.aska.game.model.Block;
import de.aska.game.model.Game;
import de.aska.game.util.EventBuilder;
import de.aska.network.Session;

import java.awt.*;
import java.util.Set;

/**
 * SOTA:
 * * event.event:
 * * * Created by KAABERT on 19.01.2017.
 */
public class SetBlockEvent extends de.aska.game.event.Event {

    private final Set<Point> targetBlocks;
    private final Block block;

    public SetBlockEvent(Rectangle triggerArea, boolean repeatable, char block, Rectangle targetArea) {
        super(triggerArea, repeatable);
        this.block = Block.Builder.build(block);
        this.targetBlocks = EventBuilder.toPoints(targetArea);
    }

    @Override
    public void execute(Session session, Game game) {
        this.targetBlocks.forEach(targetBlock -> {
            if (!game.getBlock(targetBlock).equals(block)) {
                game.setBlock(targetBlock, block);
            }
        });
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SetBlockEvent that = (SetBlockEvent) o;

        return targetBlocks.equals(that.targetBlocks) && block.equals(that.block);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + targetBlocks.hashCode();
        result = 31 * result + block.hashCode();
        return result;
    }
}
