package game.model.event;

import game.model.Game;
import game.util.EventBuilder;
import network.Session;
import org.json.JSONObject;

import java.awt.*;
import java.util.Set;

/**
 * Created DisplayEvent.java in game.util.event
 * by Arne on 11.01.2017.
 */
public class DisplayEvent extends Event {
    private final String message;
    private final int ticks;

    /**
     * <p>
     * @param triggerPoints the Points in which this Event would be triggered.
     * @param message the Message that should be printed when the player passes one of the TriggerPoints.
     * @param ticks the Time in MS the message should be displayed.
     * */
    public DisplayEvent(Set<Point> triggerPoints, String message, int ticks) {
        super(triggerPoints);
        this.message = message.replaceAll("\\\\n", "\n");
        this.ticks = ticks / 10;
    }

    public DisplayEvent(Rectangle triggerArea, String message, int ticks) {
        this(EventBuilder.toPoints(triggerArea), message, ticks);
    }

    @Override
    public void execute(Session session, Game game) {
        session.sendMessage(
                new JSONObject()
                        .put("cmd", "MESSAGE")
                        .put("msg", new JSONObject()
                                .put("text", message)
                                .put("ticks", ticks)
                        )
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DisplayEvent that = (DisplayEvent) o;

        if (ticks != that.ticks) return false;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + ticks;
        return result;
    }
}
