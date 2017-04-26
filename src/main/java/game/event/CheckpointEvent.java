package game.event;

import game.model.Game;
import network.Session;

import java.awt.*;

/**
 * Created CheckpointEvent.java in event.event
 * by Arne on 11.01.2017.
 */
public class CheckpointEvent extends Event {
    private final Point target;
    private final boolean display;

    public CheckpointEvent(Rectangle triggerArea, boolean repeatable, Point target, boolean display) {
        super(triggerArea, repeatable);
        this.target = target;
        this.display = display;
    }

    @Override
    public void execute(Session session, Game game) {
        if (!game.getPlayer().getSpawnPoint().equals(target)) {
            game.checkpoint(target);
            if (display) {
                DisplayEvent event = new DisplayEvent(this.triggerPoints, false, "⚑ Checkpoint (" + ((int) target.getX()) + ", " + ((int) target.getX()) + ")", 200);
                event.execute(session, game);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CheckpointEvent that = (CheckpointEvent) o;

        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }
}
