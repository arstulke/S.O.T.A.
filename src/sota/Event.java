package sota;

import java.awt.*;
import java.util.HashMap;

public class Event {
    public Event(EventType eventType, Rectangle trigger) {
        params = new HashMap<>();
        triggerable = true;

        this.eventype = eventType;
        this.triggerArea = trigger;
    }

    private EventType eventype;
    private Rectangle triggerArea;
    private boolean triggerable;

    private HashMap<String, Object> params;

    public void setParam(String key, Object value){
        params.put(key, value);
    }

    public boolean shouldTriggered(Point position) {
        boolean inX = position.x <= triggerArea.x && position.x >= triggerArea.x + triggerArea.height;
        boolean inY = position.y >= triggerArea.y && position.y <= triggerArea.y + triggerArea.width;

        return inX && inY;
    }

    public boolean isTriggerable() {
        return triggerable;
    }

    public EventType getEventype() {
        return eventype;
    }

    public HashMap<String,Object> getParams() {
        return params;
    }

    public void setTriggerable(boolean triggerable) {
        this.triggerable = triggerable;
    }

    public enum EventType {
        TELEPORT, DISPLAY, CHECKPOINT, END, OTHER
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventype=" + eventype +
                ", triggerArea=" + triggerArea +
                ", triggerable=" + triggerable +
                ", params=" + params +
                '}';
    }
}
