package game.util;

import game.model.Block;
import game.model.event.*;
import game.model.event.Event;

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
        return new Point(
                parseInt(point[0]),
                parseInt(point[1])
        );
    }

    private static Rectangle toRectangle(String string) {
        if (string.contains(";")) {
            String[] rectangle = string.split(";");
            String[] point1 = rectangle[0].split("\\.");
            String[] point2 = rectangle[1].split("\\.");

            int x = parseInt(point1[0]);
            int y = parseInt(point1[1]);
            int width = parseInt(point2[0]) - x;
            int height = parseInt(point2[1]) - y;
            return new Rectangle(x, y, width, height);
        } else {
            String[] point = string.split("\\.");
            int x = parseInt(point[0]);
            int y = parseInt(point[1]);
            int width = 0;
            int height = 0;
            return new Rectangle(x, y, width, height);
        }
    }

    public static Event buildEvent(String line) {
        List<String> parameters = split(line.substring(1));
        Rectangle triggerArea = toRectangle(parameters.get(0));

        String type = parameters.get(1).toLowerCase();
        parameters = parameters.subList(2, parameters.size());
        Point target;
        switch (type) {
            case Event.Type.TELEPORT:
                target = toPoint(parameters.get(0));
                return new TeleportEvent(triggerArea, target);
            case Event.Type.DISPLAY:
                String message = parameters.get(0);
                int ticks = parseInt(parameters.get(1));
                return new DisplayEvent(triggerArea, message, ticks);
            case Event.Type.CHECKPOINT:
                target = toPoint(parameters.get(0));
                boolean display = parameters.size() == 1 ? false : Boolean.valueOf(parameters.get(1));
                return new CheckpointEvent(triggerArea, target, display);
            case Event.Type.END:
                String endType = parameters.get(0);
                return new EndEvent(triggerArea, endType);
            case Event.Type.STYLE:
                String value = parameters.get(0);
                value = value.equals("null") ? null: value;

                String value2 = parameters.size() < 2 ? "null" : parameters.get(1);
                value2 = value2.equals("null") ? null: value2;

                return new StyleEvent(triggerArea, value, value2);
            case Event.Type.SETBLOCK:
                char block = parameters.get(0).toCharArray()[0];
                Rectangle targetArea = toRectangle(parameters.get(1));

                return new SetBlockEvent(triggerArea, block, targetArea);
            default:
                return null;
        }
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
}
