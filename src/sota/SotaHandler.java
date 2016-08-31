package sota;

import java.awt.*;

public class SotaHandler {

    boolean keyRight;
    boolean keyLeft;
    boolean keyUp;
    private boolean keyDown;

    private String[][] map;
    private char player;
    private Point position;

    private Point displayPosition;

    public SotaHandler(Point displayPos) {
        SOTAMapReader mr = new SOTAMapReader();
        mr.readMap(System.getProperty("user.dir") + "\\map.txt");

        player = mr.getPlayer();
        position = mr.locatePlayer();
        map = mr.getMap();

        displayPosition = new Point(0, 0);
        displayPosition.setLocation(displayPos);
    }

    public String handle(boolean[] keys) {
        updateKeysBooleans(keys);

        checkMovement();
        checkEvents();

        return display();
    }

    private void checkMovement() {
        try {
            boolean isOnLadder = map[position.x][position.y].charAt(0) == '#';

            //GravityMovement
            boolean isOnGround = !isValidChar(map[position.x + 1][position.y], true);
            if (!isOnGround && !isJumping && !isOnLadder) {
                move(1, 0, true);
            }
            isOnGround = !isValidChar(map[position.x + 1][position.y], true);


            //Left/Right Movement
            if (keyRight && !keyLeft) {
                move(0, 1, false);
            }
            if (!keyRight && keyLeft) {
                move(0, -1, false);
            }


            if (isJumping) {
                jump();
            }

            //Simple Jump Movement
            if (keyUp && !keyDown && isOnGround && !isOnLadder) {
                isJumping = true;
            }

            //Ladder Movement (Jump, Right, Left)
            if (keyUp && !keyDown && isOnLadder && !((keyRight || keyLeft) && !(keyRight && keyLeft))) {
                move(-1, 0, false);
            }
            if (keyUp && !keyDown && isOnLadder && keyRight) {
                isJumping = true;
            }
            if (keyUp && !keyDown && isOnLadder && keyLeft) {
                isJumping = true;
            }
            if (keyDown && !keyUp && (isOnLadder || map[position.x + 1][position.y].charAt(0) == '#')) {
                move(1, 0, false);
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }
    }

    private boolean isJumping = false;
    private int jumpTick = 0;

    private void jump() {
        int f = 1;


        if (jumpTick == 0) {
            move(-1, 0, false);
        } else if (jumpTick == f) {
            move(-1, 0, false);
        } else if (jumpTick == 3 * f) {
            move(1, 0, true);
        } else if (jumpTick == 4 * f) {
            move(1, 0, true);
        } else if (jumpTick == 5 * f) {
            move(1, 0, true);
            isJumping = false;
            jumpTick = -1;
        }

        jumpTick++;
    }

    private void move(int y, int x, boolean gravityCheck) {
        try {
            if (isValidChar(map[position.x + y][position.y + x], gravityCheck)) {
                position.move(position.x + y, position.y + x);
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }
    }

    private boolean isValidChar(String s, boolean gravityCheck) {
        char[] validChars = new char[]{' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char ladder = '#';
        if (s.length() == 1) {
            for (char a : validChars) {
                if (s.charAt(0) == a) {
                    return true;
                }
            }
            if (!gravityCheck) {
                if (s.charAt(0) == ladder) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    private void checkEvents() {

    }

    private void updateKeysBooleans(boolean[] keys) {
        keyRight = keys[0] || keys[1];
        keyLeft = keys[2] || keys[3];
        keyUp = keys[4] || keys[5] || keys[6];
        keyDown = keys[7] || keys[8];
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private String display() {
        int height = 9;
        int width = 32;
        String[][] d = new String[width][height];

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                try {
                    int mapDetailY = x + position.x - displayPosition.x;
                    int mapDetailX = y + position.y - displayPosition.y;

                    if (new Point(mapDetailY, mapDetailX).equals(position)) {
                        d[y][x] = String.valueOf(player);
                    } else if (String.valueOf(map[mapDetailY][mapDetailX]) == null) {
                        d[y][x] = String.valueOf(map[mapDetailY][mapDetailX]);
                    } else {
                        d[y][x] = String.valueOf(map[mapDetailY][mapDetailX]);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    d[y][x] = "§";
                    //System.out.println("ArrayIndexOutOfBoundException: " + e.getMessage());
                }

                if (d[y][x] == null)
                    d[y][x] = " ";
            }
        }

        String display = "";
        String line;

        for (int x = 0; x < height; x++) {
            line = "";
            for (int y = 0; y < width; y++) {
                line += d[y][x];
            }
            display += line + "\n";
        }


        display = display.substring(0, display.length() - 1);

        return display;
    }
}
