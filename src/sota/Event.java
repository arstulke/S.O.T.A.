package sota;

import java.awt.*;

public class Event {
    public EventType eventype;
    public Rectangle triggerArea;
    public boolean triggerable;

    public Point teleportTarget;

    public Point checkpoint;

    public String msg;
    public int time;

    public static Event newTeleportEvent(Rectangle trigger, Point target) {
        Event event = new Event();

        event.msg = null;
        event.eventype = EventType.TELEPORT;
        event.triggerArea = trigger;
        event.teleportTarget = target;
        event.time = -1;
        event.triggerable = true;

        return event;
    }

    public static Event newDisplayEvent(Rectangle trigger, int time, String msg) {
        Event event = new Event();

        event.eventype = EventType.DISPLAY;
        event.msg = msg;
        event.triggerArea = trigger;
        event.time = time;
        event.triggerable = true;

        event.teleportTarget = null;

        return event;
    }

    public static Event newCheckpointEvent(Rectangle triggeredArea, Point checkpoint) {
        Event event = new Event();

        event.eventype = EventType.CHECKPOINT;
        event.triggerArea = triggeredArea;
        event.checkpoint = checkpoint;
        event.triggerable = true;

        event.msg = null;
        event.teleportTarget = null;
        event.time = 0;

        return event;
    }

    public boolean shouldTriggered(Point position) {
        boolean inX = position.x <= triggerArea.x && position.x >= triggerArea.x + triggerArea.height;
        boolean inY = position.y >= triggerArea.y && position.y <= triggerArea.y + triggerArea.width;

        return inX && inY;
    }

    public boolean isTriggerable() {
        return triggerable;
    }

    public void execute(Object... object) {
        switch (eventype) {
            case TELEPORT:
                char targetChar = ((char[][]) object[0])[teleportTarget.x][teleportTarget.y];
                if (SotaHandler.isPassableChar(targetChar, true))
                    ((Point) object[1]).setLocation(teleportTarget);
                break;

            case DISPLAY:
                System.out.println("msg: " + msg);
                break;

            case CHECKPOINT:
                ((Point) object[2]).setLocation(checkpoint);
                System.out.println("checkpoint");
                triggerable = false;
                break;
        }
    }

    public enum EventType {
        TELEPORT, DISPLAY, CHECKPOINT
    }

    @Override
    public String toString() {
        switch (eventype) {
            case TELEPORT:
                return "Event {" +
                        "eventype=" + eventype +
                        ", triggerArea=" + triggerArea +
                        ", triggerable=" + triggerable +
                        ", teleportTarget=" + teleportTarget +
                        "}";

            case DISPLAY:
                return "Event{" +
                        "eventype=" + eventype +
                        ", triggerArea=" + triggerArea +
                        ", msg='" + msg + '\'' +
                        ", time=" + time +
                        '}';

            case CHECKPOINT:
                return "Event{" +
                        "eventype=" + eventype +
                        ", triggerArea=" + triggerArea +
                        ", checkpoint=" + checkpoint +
                        '}';
        }

        return "Event{" +
                "eventype=" + eventype +
                ", triggerArea=" + triggerArea +
                ", teleportTarget=" + teleportTarget +
                ", msg='" + msg + '\'' +
                ", time=" + time +
                ", triggerable=" + triggerable +
                '}';
    }
}
