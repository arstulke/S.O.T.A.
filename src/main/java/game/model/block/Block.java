package game.model.block;

/**
 * Created Block.java in game.model
 * by Arne on 11.01.2017.
 */
public class Block {
    private final char ch;
    private final double slowDown;

    private final boolean solid;
    private final boolean semiSolidGround;
    private final boolean semiSolidTop;

    private Block(char ch, double slowDown, boolean solid, boolean semiSolidGround, boolean semiSolidTop) {
        this.ch = ch;
        this.slowDown = slowDown;
        this.solid = solid;
        this.semiSolidGround = semiSolidGround;
        this.semiSolidTop = semiSolidTop;
    }

    public static Block build(char c) {
        String nonSolidChars = "abcdefghijklmnopqrstuvwxyz#_ ";
        String semiSolidGroundChars = "#_";
        String semiSolidTopChars = "#";

        double slowDown = c == '#' ? 2f : 1f;

        if (!nonSolidChars.contains("" + c)) {
            return new Block(c, slowDown, true, false, false);
        } else {
            boolean semiSolidGround = semiSolidGroundChars.contains("" + c);
            boolean semiSolidTop = semiSolidTopChars.contains("" + c);

            return new Block(c, slowDown, false, semiSolidGround, semiSolidTop);
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

    public double getSlowDown() {
        return slowDown;
    }
}
