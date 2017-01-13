package game.model.event;

import game.model.Game;
import network.Session;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created Event.java in game.model
 * by Arne on 11.01.2017.
 */
public abstract class Event {
    final Rectangle triggerArea;

    Event(Rectangle triggerArea) {
        this.triggerArea = triggerArea;
    }

    public abstract void execute(Session session, Game game);

    public Set<Point> getTriggerPoints() {
        Set<Point> triggerPoints = new HashSet<>();

        int startX = (int) triggerArea.getX();
        int endX = (int) (triggerArea.getWidth() + triggerArea.getX());
        int startY = (int) triggerArea.getY();
        int endY = (int) (triggerArea.getHeight() + triggerArea.getY());

        int minX = Math.min(startX, endX);
        int maxX = Math.max(startX, endX);

        int minY = Math.min(startY, endY);
        int maxY = Math.max(startY, endY);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                triggerPoints.add(new Point(x, y));
            }
        }
        return triggerPoints;
    }

    public class Type {
        public static final String CHECKPOINT = "checkpoint";
        public static final String TELEPORT = "teleport";
        public static final String DISPLAY = "display";
        public static final String END = "end";
        public static final String STYLE = "style";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return getTriggerPoints().equals(event.getTriggerPoints());
    }

    @Override
    public int hashCode() {
        return getTriggerPoints().hashCode();
    }
}
