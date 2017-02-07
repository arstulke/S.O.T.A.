package game.model.event;

import game.model.Game;
import game.util.EventBuilder;
import network.Session;

import java.awt.*;
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

    public enum Type {
        CHECKPOINT("checkpoint"),
        TELEPORT("teleport"),
        DISPLAY("display"),
        END("end"),
        STYLE("style"),
        SET_BLOCK("setblock"),
        EXECUTE_CONDITION("if_condition"),
        SET_CONDITION("set_condition");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static Type find(String s) {
            for (Type type : Type.values()) {
                if (type.name.equals(s)) {
                    return type;
                }
            }

            return null;
        }
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
