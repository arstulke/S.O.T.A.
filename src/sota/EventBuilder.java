package sota;

import java.awt.*;

/**
 * Created EventBuilder in sota
 * by ARSTULKE on 04.11.2016.
 */
public class EventBuilder {
    public static Event newTeleportEvent(Rectangle trigger, Point target) {
        Event event = new Event(Event.EventType.TELEPORT, trigger);
        event.setParam("teleportTarget", target);
        return event;
    }

    public static Event newDisplayEvent(Rectangle trigger, int time, String msg) {
        Event event = new Event(Event.EventType.DISPLAY, trigger);
        event.setParam("msg", msg);
        event.setParam("time", time);
        return event;
    }

    public static Event newCheckpointEvent(Rectangle triggerArea, Point checkpoint) {
        Event event = new Event(Event.EventType.CHECKPOINT, triggerArea);
        event.setParam("checkpoint", checkpoint);
        return event;
    }

    public static Event newEvent(Rectangle triggerArea, int id) {
        Event event = new Event(Event.EventType.OTHER, triggerArea);
        event.setParam("id", id);
        return event;
    }

    public static Event newFinishEvent(Rectangle triggerArea, String command) {
        Event event = new Event(Event.EventType.END, triggerArea);
        event.setParam("end", command);
        return event;
    }
}
