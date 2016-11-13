package de.sota.game;

import java.awt.*;

/**
 * Created EventBuilder in sota
 * by ARSTULKE on 04.11.2016.
 */
public class EventBuilder {
    public static de.sota.game.Event newTeleportEvent(Rectangle trigger, Point target) {
        de.sota.game.Event event = new de.sota.game.Event(de.sota.game.Event.EventType.TELEPORT, trigger);
        event.setParam("teleportTarget", target);
        return event;
    }

    public static de.sota.game.Event newDisplayEvent(Rectangle trigger, int time, String msg) {
        de.sota.game.Event event = new de.sota.game.Event(de.sota.game.Event.EventType.DISPLAY, trigger);
        event.setParam("msg", msg);
        event.setParam("time", time);
        return event;
    }

    public static de.sota.game.Event newCheckpointEvent(Rectangle triggerArea, Point checkpoint) {
        de.sota.game.Event event = new de.sota.game.Event(de.sota.game.Event.EventType.CHECKPOINT, triggerArea);
        event.setParam("checkpoint", checkpoint);
        return event;
    }

    public static de.sota.game.Event newEvent(Rectangle triggerArea, int id) {
        de.sota.game.Event event = new de.sota.game.Event(de.sota.game.Event.EventType.OTHER, triggerArea);
        event.setParam("id", id);
        return event;
    }

    public static de.sota.game.Event newFinishEvent(Rectangle triggerArea, String command) {
        de.sota.game.Event event = new de.sota.game.Event(de.sota.game.Event.EventType.END, triggerArea);
        event.setParam("end", command);
        return event;
    }
}
