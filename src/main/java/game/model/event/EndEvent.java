package game.model.event;

import game.model.Game;
import game.model.Statistics;
import network.Session;
import network.WebSocketHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

/**
 * Created EndEvent.java in game.model.event
 * by Arne on 11.01.2017.
 */
public class EndEvent extends Event {
    private final String endType;

    public EndEvent(Rectangle triggerArea, String endType) {
        super(triggerArea);
        this.endType = endType;
    }

    @Override
    public void execute(Session session, Game game) {
        if (endType.equals("finish")) {
            session.sendMessage(
                    new JSONObject()
                            .put("cmd", "END")
                            .put("msg", "finish")
            );

            WebSocketHandler.addStatistics(game.buildStatistics());
            JSONArray stats = WebSocketHandler.buildStatistics(Statistics.TICKS);
            System.out.println(stats);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EndEvent endEvent = (EndEvent) o;

        return endType.equals(endEvent.endType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + endType.hashCode();
        return result;
    }
}
