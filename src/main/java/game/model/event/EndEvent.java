package game.model.event;

import game.model.Game;
import game.model.Player;
import game.model.Statistics;
import network.Session;
import org.json.JSONObject;

import java.awt.*;

/**
 * Created EndEvent.java in game.model.event
 * by Arne on 11.01.2017.
 */
public class EndEvent extends Event {
    private final String endType;

    public EndEvent(Rectangle triggerArea, String endType) {
        super(triggerArea, false);
        this.endType = endType;
    }

    @Override
    public void execute(Session session, Game game) {
        if (endType.equals("finish")) {
            Statistics stats = game.buildStatistics();

            session.sendMessage(
                    new JSONObject()
                            .put("cmd", "END")
                            .put("msg", "finish")
            );

            Player p = game.getPlayer();
            p.setSpawnPoint(p.getPosition());


            String msg1 = "<b>You reached the finish!</b>";
            String msg2 = String.format("Your Statistics:\n Fails:%d\n Time:%s", stats.getFails(), ((double) stats.getTicks()) / 10.0);

            DisplayEvent event1 = new DisplayEvent(this.triggerPoints, false, msg1, 1000);
            DisplayEvent event2 = new DisplayEvent(this.triggerPoints, false, msg2, 1000);


            event1.execute(session, game);
            event2.execute(session, game);
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
