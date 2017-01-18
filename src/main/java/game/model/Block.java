package game.model;

/**
 * Created Block.java in game.model
 * by Arne on 11.01.2017.
 */
public class Block {
    private final char ch;
    private final double horizontalSlowDown;
    private final double verticalSlowDown;

    private final boolean solid;
    private final boolean semiSolidGround;
    private final boolean semiSolidTop;

    private Block(char ch, double horizontalSlowDown, double verticalSlowDown, boolean solid, boolean semiSolidGround, boolean semiSolidTop) {
        this.ch = ch;
        this.horizontalSlowDown = horizontalSlowDown;
        this.verticalSlowDown = verticalSlowDown;
        this.solid = solid;
        this.semiSolidGround = semiSolidGround;
        this.semiSolidTop = semiSolidTop;
    }

    public static Block build(char c) {
        String nonSolidChars = "abcdefghijklmnopqrstuvwxyz#*_ ";
        String semiSolidGroundChars = "#_";
        String semiSolidTopChars = "#";

        double horizontalSlowDown = 1.0;
        double verticalSlowDown = c == '#' ? 2.0 : 1.0;

        if (!nonSolidChars.contains("" + c)) {
            return new Block(c, horizontalSlowDown, verticalSlowDown, true, false, false);
        } else {
            boolean semiSolidGround = semiSolidGroundChars.contains("" + c);
            boolean semiSolidTop = semiSolidTopChars.contains("" + c);

            return new Block(c, horizontalSlowDown, verticalSlowDown, false, semiSolidGround, semiSolidTop);
        }
    }

    public boolean hasSolidGround() {
        return semiSolidGround;
    }

    public boolean hasSolidTop() {
        return semiSolidTop;
    }

    public boolean isSolid() {
        return solid;
    }

    public char getChar() {
        return ch;
    }

    public double getHorizontalSlowDown() {
        return horizontalSlowDown;
    }

    public double getVerticalSlowDown() {
        return verticalSlowDown;
    }
}