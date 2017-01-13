package game.util;

import game.model.Game;
import game.model.block.Block;

import java.awt.*;

/**
 * Created GameRenderer.java in game
 * by Arne on 11.01.2017.
 */
public class GameRenderer {
    public static final GameRenderer DEFAULT = new GameRenderer(31, 9);

    private final int width;
    private final int height;
    private String backgroundColor;
    private String foregroundColor;
    private final Point p;

    private GameRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.backgroundColor = "#ffffff";
        this.foregroundColor = "#000000";

        this.p = new Point(width / 2, (int) (height * 0.5));
    }

    private GameRenderer(int width, int height, String backgroundColor, String foregroundColor) {
        this(width, height);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    public String render(Game game) {
        String out = "";
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point position = game.getPlayer().getPosition();
                int X = (int) (x + position.getX() - p.getX());
                int Y = (int) (y + position.getY() - p.getY());

                Point blockPosition = new Point(X, Y);
                Block block = game.getBlock(blockPosition);
                char ch = ' ';
                if (x == p.getX() && y == p.getY()) {
                    ch = game.getPlayer().getPlayerChar();
                } else if (block != null) {
                    ch = block.getChar();
                } else {
                    if ((X == -1 || X == game.getWidth() + 1) && Y >= -1 && Y <= game.getHeight() + 1) {
                        ch = '|';
                    } else if ((Y == -1 || Y == game.getHeight() + 1) && X >= 0 && X <= game.getWidth()) {
                        ch = '-';
                    }
                }
                out += ch;
            }
            out += "\n";
        }

        out = out.substring(0, out.length() - 1);
        return out;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public GameRenderer copy() {
        return new GameRenderer(this.width, this.height, this.backgroundColor, this.foregroundColor);
    }
}
