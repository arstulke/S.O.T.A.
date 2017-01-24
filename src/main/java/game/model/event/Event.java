package game.model.event;

import game.model.Game;
import game.util.EventBuilder;
import network.Session;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created Event.java in game.model
 * by Arne on 11.01.2017.
 */
public abstract class Event {
    final Set<Point> triggerPoints;
    private final boolean repeatable;

    Event(Set<Point> triggerPoints, boolean repeatable) {
        this.triggerPoints = triggerPoints;
        this.repeatable = repeatable;
    }

    Event(Rectangle triggerArea, boolean repeatable) {
        this(EventBuilder.toPoints(triggerArea), repeatable);
    }

    public abstract void execute(Session session, Game game);

    public Set<Point> getTriggerPoints() {
        return triggerPoints;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public class Type {
        public static final String CHECKPOINT = "checkpoint";
        public static final String TELEPORT = "teleport";
        public static final String DISPLAY = "display";
        public static final String END = "end";
        public static final String STYLE = "style";
        public static final String SET_BLOCK = "setblock";
        public static final String EXECUTE_CONDITION = "if_condition";
        public static final String SET_CONDITION = "set_condition";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return triggerPoints.equals(event.triggerPoints);
    }

    @Override
    public int hashCode() {
        return triggerPoints.hashCode();
    }
}
