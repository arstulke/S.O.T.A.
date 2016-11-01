package sota;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class EventReader {
    public static Event readEvent(String line) {
        String trigger = line.substring(1, line.indexOf(" "));
        String command = line.substring(line.indexOf(" ") + 1);

        Rectangle triggerArea = getTriggerArea(trigger);
        Event.EventType cmdType = getEventType(command.substring(0, command.indexOf(" ")));

        command = command.substring(command.indexOf(" ") + 1);
        if (cmdType != null) {
            int x, y;
            switch (cmdType) {
                case TELEPORT:
                    x = Integer.parseInt(command.substring(0, command.indexOf(".")));
                    y = Integer.parseInt(command.substring(command.indexOf(".") + 1));
                    return Event.newTeleportEvent(triggerArea, new Point(x, y));

                case DISPLAY:
                    String[] params = new String[2];
                    boolean inString = false;
                    for(int i = 0; i < command.length(); i++) {
                        char charAt = command.charAt(i);
                        if(charAt == '"') {
                            inString = !inString;
                        } else if(charAt == ' ' && !inString) {
                            params[0] = command.substring(0, i).replace("\"", "");
                            params[1] = command.substring(i + 1);
                            break;
                        }
                    }
                    return Event.newDisplayEvent(triggerArea, Integer.parseInt(params[1]), params[0]);

                case CHECKPOINT:
                    x = Integer.parseInt(command.substring(0, command.indexOf(".")));
                    y = Integer.parseInt(command.substring(command.indexOf(".") + 1));
                    return Event.newCheckpointEvent(triggerArea, new Point(x, y));
            }
        }

        return null;
    }

    private static Rectangle getTriggerArea(String trigger) {
        Rectangle triggerArea;
        if (trigger.contains(";")) {
            String[] area = trigger.split(";");
            int x = Integer.parseInt(area[0].substring(0, trigger.indexOf(".")));
            int y = Integer.parseInt(area[0].substring(trigger.indexOf(".") + 1));
            int width = x - Integer.parseInt(area[1].substring(0, trigger.indexOf(".")));
            int height = y - Integer.parseInt(area[1].substring(trigger.indexOf(".") + 1));

            triggerArea = new Rectangle(x, y, width, height);
        } else {
            int x = Integer.parseInt(trigger.substring(0, trigger.indexOf(".")));
            int y = Integer.parseInt(trigger.substring(trigger.indexOf(".") + 1));
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
        } else return null;
    }
}
