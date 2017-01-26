package game.util;

import game.CoordinateMap;
import game.model.Block;
import game.model.Game;
import game.model.Player;
import game.model.event.Event;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created GameBuilder in game.model
 * by ARSTULKE on 25.01.2017.
 */
public class GameBuilder {
    private final Player player;
    private final CoordinateMap<Block> blocks;
    private final CoordinateMap<List<Event>> events;
    private final GameRenderer gameRenderer;
    private final Map<String, String> conditions;
    private final String title;
    private final Set<String> resources;

    GameBuilder(Player player, Map<Point, Block> blocks, Map<Point, List<Event>> events, GameRenderer gameRenderer, Map<String, String> conditions, String title, Set<String> resources) {
        this.title = title;

        this.player = player;
        this.blocks = new CoordinateMap<>(blocks);
        this.events = new CoordinateMap<>(events);

        this.gameRenderer = gameRenderer;

        this.conditions = conditions;

        this.resources = resources;
    }

    Game build() {
        return new Game(player.copy(), blocks.copy(), events.copy(), gameRenderer.copy(), new HashMap<>(conditions), title);
    }

    Set<String> getResources() {
        return resources;
    }

    Set<String> getTextures(String title) {
        Function<Character, String> function = character -> {
            String ch = character + "";
            if (Character.isLowerCase(ch.charAt(0))) {
                ch = "k" + ch;
            } else {
                ch = ch.toLowerCase();
            }

            ch = ch
                    .replace("/", "slash")
                    .replace("\\", "backslash")
                    .replace(":", "double")
                    .replace("*", "star")
                    .replace("?", "questionmark")
                    .replace("\"", "syno")
                    .replace("<", "smaller")
                    .replace(">", "bigger")
                    .replace("|", "stick")
                    .replace(" ", "space")
                    .replace("^", "spike")
                    .replace("#", "hashtag")
                    .replace("_", "k_");

            return "/texture?map=" + title + "&name=" + ch;
        };

        Set<String> textures = blocks.stream().map(Map.Entry::getValue).map(Block::getChar).collect(Collectors.toSet()).stream().map(function).collect(Collectors.toSet());
        textures.add(function.apply(player.getPlayerChar()));

        return textures;
    }

    public String getTitle() {
        return title;
    }
}
