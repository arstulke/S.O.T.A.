package de.aska.game.model;

/**
 * Created Block.java in game.model
 * by Arne on 11.01.2017.
 */
@SuppressWarnings("SimplifiableIfStatement")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        if (ch != block.ch) return false;
        if (Double.compare(block.horizontalSlowDown, horizontalSlowDown) != 0) return false;
        if (Double.compare(block.verticalSlowDown, verticalSlowDown) != 0) return false;
        if (solid != block.solid) return false;
        if (semiSolidGround != block.semiSolidGround) return false;
        return semiSolidTop == block.semiSolidTop;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) ch;
        temp = Double.doubleToLongBits(horizontalSlowDown);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(verticalSlowDown);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (solid ? 1 : 0);
        result = 31 * result + (semiSolidGround ? 1 : 0);
        result = 31 * result + (semiSolidTop ? 1 : 0);
        return result;
    }

    public static class Builder {
        @SuppressWarnings("SpellCheckingInspection")
        public static Block build(char c) {
            String nonSolidChars = "abcdefghijklmnopqrstuvwxyz#*_äöü .,'`´";
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
    }
}
