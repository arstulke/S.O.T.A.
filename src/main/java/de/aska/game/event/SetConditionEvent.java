package de.aska.game.event;

import de.aska.game.model.Game;
import de.aska.network.Session;

import java.awt.*;

/**
 * Created SetConditionEvent in event.event
 * by ARSTULKE on 23.01.2017.
 */
public class SetConditionEvent extends de.aska.game.event.Event {
    private final String name;
    private final String value;

    public SetConditionEvent(Rectangle triggerArea, boolean repeatable, String name, String value) {
        super(triggerArea, repeatable);
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(Session session, Game game) {
        game.setCondition(name, value);
    }
}
