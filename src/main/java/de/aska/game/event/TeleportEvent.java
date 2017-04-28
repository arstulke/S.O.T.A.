package de.aska.game.event;

import de.aska.game.model.Game;
import de.aska.network.Session;

import java.awt.*;

/**
 * Created TeleportEvent.java in game.util.event
 * by Arne on 11.01.2017.
 */
public class TeleportEvent extends de.aska.game.event.Event {
    private final Point target;

    public TeleportEvent(Rectangle triggerArea, boolean repeatable, Point target) {
        super(triggerArea, repeatable);
        this.target = target;
    }

    @Override
    public void execute(Session session, Game game) {
        game.getPlayer().setPosition(target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TeleportEvent that = (TeleportEvent) o;

        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }
}
