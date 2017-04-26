package game.util;

import game.event.*;
import game.event.Event;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;

/**
 * Created EventBuilder.java in game.util.event
 * by Arne on 11.01.2017.
 */
public class EventBuilder {

    private static Point toPoint(String string) {
        String[] point = string.split("\\.");
        if (point[1].contains(";")) {
            throw new IllegalArgumentException("You aren't allowed to set an area here.");
        }
        return new Point(
                parseInt(point[0]),
                parseInt(point[1])
        );
    }

    private static Rectangle toRectangle(String string) {
        if (string.contains(";")) {
            try {
                String[] rectangle = string.split(";");
                String[] point1 = rectangle[0].split("\\.");
                String[] point2 = rectangle[1].split("\\.");

                int x = parseInt(point1[0]);
                int y = parseInt(point1[1]);
                int width = parseInt(point2[0]) - x;
                int height = parseInt(point2[1]) - y;
                return new Rectangle(x, y, width, height);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("You have to set an area in the following syntax: \"x1.y1;x2.y2\"");
            }
        } else {
            try {
                String[] point = string.split("\\.");
                int x = parseInt(point[0]);
                int y = parseInt(point[1]);
                int width = 0;
                int height = 0;
                return new Rectangle(x, y, width, height);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("You have to set a point in the following syntax: \"x.y\"");
            }
        }
    }

    static Event buildEvent(String line) {
        String l = line.startsWith("~~") ? line.substring(1) : line;
        List<String> parameters = split(l.substring(1));
        Rectangle triggerArea = toRectangle(parameters.get(0));

        parameters = parameters.subList(1, parameters.size());
        return buildThinEvent(triggerArea, line.charAt(1) == '~', parameters);
    }

    private static Event buildThinEvent(Rectangle triggerArea, boolean repeatable, List<String> parameters) {
        String typeName = parameters.get(0).toLowerCase();
        Event.Type type = Event.Type.find(typeName);
        parameters = parameters.subList(1, parameters.size());
        Point target;
        if (type != null) {
            switch (type) {
                case TELEPORT:
                    target = toPoint(parameters.get(0));
                    return new TeleportEvent(triggerArea, repeatable, target);
                case DISPLAY:
                    String message = parameters.get(0);
                    int ticks = parseInt(parameters.get(1));
                    return new DisplayEvent(triggerArea, repeatable, message, ticks);
                case CHECKPOINT:
                    target = toPoint(parameters.get(0));
                    boolean display = parameters.size() == 1 ? false : Boolean.valueOf(parameters.get(1));
                    return new CheckpointEvent(triggerArea, repeatable, target, display);
                case END:
                    String endType = parameters.get(0);
                    return new EndEvent(triggerArea, endType);
                case STYLE:
                    String value = parameters.get(0);
                    value = value.equals("null") ? null : value;

                    String value2 = parameters.size() < 2 ? "null" : parameters.get(1);
                    value2 = value2.equals("null") ? null : value2;

                    return new StyleEvent(triggerArea, value, value2);
                case SET_BLOCK:
                    char block = parameters.get(0).toCharArray()[0];
                    Rectangle targetArea = toRectangle(parameters.get(1));

                    return new SetBlockEvent(triggerArea, repeatable, block, targetArea);
                case EXECUTE_CONDITION:
                    String name = parameters.get(0);
                    String expectedValue = parameters.get(1);

                    Event event = buildChainedEvent(parameters);
                    return new ExecuteConditionEvent(triggerArea, repeatable, name, expectedValue, event);
                case SET_CONDITION:
                    name = parameters.get(0);
                    value = parameters.get(1);
                    return new SetConditionEvent(triggerArea, repeatable, name, value);
            }
        }
        return null;
    }

    private static Event buildChainedEvent(List<String> parameters) {
        List<String> params;
        boolean newEventRepeatable;
        if (parameters.indexOf("-->") == -1 || (parameters.indexOf("->") < parameters.indexOf("-->") && parameters.indexOf("->") != -1)) {
            params = parameters.subList(parameters.indexOf("->") + 1, parameters.size());
            newEventRepeatable = false;
        } else if (parameters.indexOf("->") != -1 || (parameters.indexOf("-->") < parameters.indexOf("->") && parameters.indexOf("-->") != -1)) {
            params = parameters.subList(parameters.indexOf("-->") + 1, parameters.size());
            newEventRepeatable = true;
        } else {
            throw new IllegalArgumentException("You have to set an event that should be executed.");
        }
        return buildThinEvent(null, newEventRepeatable, params);
    }

    private static List<String> split(String line) {
        List<String> strings = new ArrayList<>();
        boolean inString = false;

        int lastPartEnd = 0;
        for (int x = 0; x < line.length(); x++) {
            if (line.charAt(x) == '"') {
                inString = !inString;
            }

            if ((!inString && line.charAt(x) == ' ')) {
                String string = line.substring(lastPartEnd, x);
                if (string.startsWith("\"") && string.endsWith("\"")) {
                    string = string.substring(1, string.length() - 1);
                }

                if (string.length() > 0) {
                    strings.add(strings.size(), string);
                }
                lastPartEnd = x + 1;
            } else if (x == line.length() - 1) {
                String string = line.substring(lastPartEnd);
                if (string.startsWith("\"") && string.endsWith("\"")) {
                    string = string.substring(1, string.length() - 1);
                }

                strings.add(strings.size(), string);
            }
        }

        return strings;
    }

    public static Set<Point> toPoints(Rectangle triggerArea) {
        if (triggerArea != null) {
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
        return null;
    }
}
