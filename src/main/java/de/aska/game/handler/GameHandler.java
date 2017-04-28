package de.aska.game.handler;

import de.aska.game.model.Game;
import de.aska.network.Session;
/**
 * Created GameHandler.java in game
 * by Arne on 11.01.2017.
 */
public interface GameHandler {
    boolean update(Session session, Game game);
}
