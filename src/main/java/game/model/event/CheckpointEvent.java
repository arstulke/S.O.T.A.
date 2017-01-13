package game.model.event;

import game.model.Game;
import network.Session;

import java.awt.*;

/**
 * Created CheckpointEvent.java in game.model.event
 * by Arne on 11.01.2017.
 */
public class CheckpointEvent extends Event {
    private final Point target;

    public CheckpointEvent(Rectangle triggerArea, Point target) {
        super(triggerArea);
        this.target = target;
    }

    @Override
    public void execute(Session session, Game game) {
        game.getPlayer().setSpawnPoint(target);
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
