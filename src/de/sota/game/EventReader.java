package de.sota.game;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class EventReader {
    public static Event readEvent(String line) {
        if (line.length() > 1) {
            String trigger = line.substring(1, line.indexOf(" "));
            String command = line.substring(line.indexOf(" ") + 1);

            Rectangle triggerArea = getTriggerArea(trigger);
            Event.EventType cmdType = getEventType(command.substring(0, command.indexOf(" ")));

            command = command.substring(command.indexOf(" ") + 1);
            if (cmdType != null) {
                int x, y;
                switch (cmdType) {
                    case TELEPORT:
                        x = Integer.parseInt(command.substring(command.indexOf(".") + 1));
                        y = Integer.parseInt(command.substring(0, command.indexOf(".")));
                        return EventBuilder.newTeleportEvent(triggerArea, new Point(x, y));

                    case DISPLAY:
                        String[] params = new String[2];
                        boolean inString = false;
                        for (int i = 0; i < command.length(); i++) {
                            char charAt = command.charAt(i);
                            if (charAt == '"') {
                                inString = !inString;
                            } else if (charAt == ' ' && !inString) {
                                params[0] = command.substring(0, i).replace("\"", "").replace("\\n", "\n");
                                params[1] = command.substring(i + 1);
                                break;
                            }
                        }
                        return EventBuilder.newDisplayEvent(triggerArea, Integer.parseInt(params[1]), params[0]);

                    case CHECKPOINT:
                        x = Integer.parseInt(command.substring(command.indexOf(".") + 1));
                        y = Integer.parseInt(command.substring(0, command.indexOf(".")));
                        return EventBuilder.newCheckpointEvent(triggerArea, new Point(x, y));
                    case END:
                        return EventBuilder.newFinishEvent(triggerArea, command);
                    case OTHER:
                        int id = Integer.parseInt(command);
                        return EventBuilder.newEvent(triggerArea, id);
                }
            }
        }
        return null;
    }

    private static Rectangle getTriggerArea(String trigger) {
        Rectangle triggerArea;
        if (trigger.contains(";")) {
            String[] area = trigger.split(";");
            int x = Integer.parseInt(area[0].substring(trigger.indexOf(".") + 1));
            int y = Integer.parseInt(area[0].substring(0, trigger.indexOf(".")));
            int width = x - Integer.parseInt(area[1].substring(trigger.indexOf(".") + 1));
            int height = y - Integer.parseInt(area[1].substring(0, trigger.indexOf(".")));

            triggerArea = new Rectangle(x, y, width, height);
        } else {
            int x = Integer.parseInt(trigger.substring(trigger.indexOf(".") + 1));
            int y = Integer.parseInt(trigger.substring(0, trigger.indexOf(".")));
            triggerArea = new Rectangle(x, y, 0, 0);
        }

        return triggerArea;
    }

    private static Event.EventType getEventType(final String cmdType) {
        if (StringUtils.equalsIgnoreCase(cmdType, "teleport")) {
            return Event.EventType.TELEPORT;
        } else if (StringUtils.equalsIgnoreCase(cmdType, "display")) {
            return Event.EventType.DISPLAY;
        } else if (StringUtils.equalsIgnoreCase(cmdType, "checkpoint")) {
            return Event.EventType.CHECKPOINT;
        } else if (StringUtils.equalsIgnoreCase(cmdType, "end")) {
            return Event.EventType.END;
        } else return null;
    }
}
