package sota;

import java.awt.*;
import java.util.List;

public class SotaHandler {

    private boolean keyRight; //is *Key is pressed
    private boolean keyLeft; //is *Key is pressed
    private boolean keyUp; //is *Key is pressed
    private boolean keyDown; //is *Key is pressed

    private boolean isJumping = false; //is Jumping
    private int jumpTick = 0; //UP UP STAY DOWN DOWN
    private boolean isAutoJumping = false; //small jump 1 step
    private int autoJumpTick = 0; //UP LEFT/RIGHT
    private int autoJumpDir = 0; //left -1      right 1

    private char[][] map; //charMap
    private List<Event> eventList;
    private char player; //Player Character
    public Point position; //Player Position

    private Point displayPosition;

    //Event fields
    private Point checkpoint = new Point();
    private String message = null;
    private int messageTicks = -1;

    public SotaHandler(Point displayPos) {
        loadMap(System.getProperty("user.dir") + "\\map.txt");

        displayPosition = new Point(0, 0);
        displayPosition.setLocation(displayPos);
    }

    public SotaHandler(Point displayPos, String path) {
        loadMap(path);

        displayPosition = new Point(0, 0);
        displayPosition.setLocation(displayPos);
    }

    private void loadMap(String path) {
        SOTAMapReader mr = new SOTAMapReader();
        mr.readMap(path);

        player = mr.getPlayer();
        position = mr.locatePlayer();
        map = convertToCharArray(mr.getMap());
        eventList = mr.getEventList();
        checkpoint.setLocation(position);
    }

    private char[][] convertToCharArray(String[][] map) {
        String[] arr1 = map[0];
        char[][] tmp = new char[map.length][arr1.length];
        for (int y = 0; y < map.length; y++) {
            String[] array = map[y];
            for (int x = 0; x < array.length; x++) {
                tmp[y][x] = array[x].toCharArray()[0];
            }
        }
        return tmp;
    }

    public String controlMovement(boolean kRight, boolean kLeft, boolean kUp, boolean kDown) {
        keyRight = kRight;
        keyLeft = kLeft;
        keyUp = kUp;
        keyDown = kDown;

        checkMovement();
        checkEvents();
        checkDie();

        return display();
    }

    private void checkDie() {
        if (map[position.x + 1][position.y] == '^') {
            position.setLocation(checkpoint);
        }
    }

    private void checkMovement() {
        try {
            boolean isOnLadder = map[position.x][position.y] == '#';
            boolean isOnGround = !isPassableChar(map[position.x + 1][position.y], true);

            //GravityMovement
            if (!isOnGround && !isJumping && !isAutoJumping && !isOnLadder) {
                move(1, 0, true); //move 1 down
            }
            isOnGround = !isPassableChar(map[position.x + 1][position.y], true);

            //Left/Right Movement
            if (keyRight && !keyLeft) {
                move(0, 1, false); //move right
            } else if (!keyRight && keyLeft) {
                move(0, -1, false); //move left
            }

            if (isAutoJumping) {
                autoJump();
            } else if (isJumping) {
                jump();
            }

            if (!keyDown && isOnGround && !isOnLadder) {
                //Jump Movement
                if (keyUp) {
                    isJumping = true;
                }
                //Auto Jump Movement
                else if (keyLeft && !isPassableChar(map[position.x][position.y - 1], false) && isPassableChar(map[position.x - 1][position.y - 1], false)) {
                    isAutoJumping = true;
                    autoJumpDir = -1;
                } else if (keyRight && !isPassableChar(map[position.x][position.y + 1], false) && isPassableChar(map[position.x - 1][position.y + 1], false)) {
                    isAutoJumping = true;
                    autoJumpDir = 1;
                }
            }

            //Ladder Movement (Jump, Right, Left)
            if (keyUp && !keyDown && isOnLadder && !((keyRight || keyLeft) && !(keyRight && keyLeft))) {
                move(-1, 0, false); //move up
            }
            if (keyUp && !keyDown && isOnLadder && keyRight) {
                isJumping = true; //jump from ladder to right
            }
            if (keyUp && !keyDown && isOnLadder && keyLeft) {
                isJumping = true; //jump from ladder to left
            }
            if (keyDown && !keyUp && (isOnLadder || moveToDefinedChar(1, 0, '#'))) {
                move(1, 0, false); //move down
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }
    }

    private void autoJump() {
        int f = 1;

        if (autoJumpTick == 0) {
            move(-1, 0, false);
        } else if (autoJumpTick == f) {
            move(0, autoJumpDir, false);
            isAutoJumping = false;
            autoJumpTick = -1;
        }

        autoJumpTick++;
    }

    private boolean moveToDefinedChar(int x, int y, char character) {
        return map[position.x + x][position.y + y] == character;
    }

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
            if (isPassableChar(map[position.x + y][position.y + x], gravityCheck)) {
                position.move(position.x + y, position.y + x);
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public static boolean isPassableChar(char s, boolean gravityCheck) {
        char[] validChars = new char[]{' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char ladder = '#';
        for (char a : validChars) {
            if (s == a) {
                return true;
            }
        }
        if (!gravityCheck) {
            if (s == ladder) {
                return true;
            }
        }

        return false;
    }

    private void checkEvents() {
        for (Event event : eventList) {
            boolean execute = event.shouldTriggered(position) && event.isTriggerable();
            if (execute) {
                event.execute(map, position, checkpoint);
                if (event.eventype == Event.EventType.DISPLAY) {
                    message = event.msg;
                    messageTicks = event.time;
                }
            }
        }

        if(messageTicks == 0) {
            message = null;
            messageTicks = -1;
        } else if(messageTicks > 0) {
            messageTicks--;
        }

        eventList.stream().filter(event -> event.eventype == Event.EventType.CHECKPOINT && !event.shouldTriggered(checkpoint)).forEach(event -> event.triggerable = true);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private String display() {
        int height = 9;
        int width = 32;
        char[][] d = new char[width][height];

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                try {
                    int mapDetailY = x + position.x - displayPosition.x;
                    int mapDetailX = y + position.y - displayPosition.y;

                    if (new Point(mapDetailY, mapDetailX).equals(position)) {
                        d[y][x] = player;
                    } else {
                        d[y][x] = map[mapDetailY][mapDetailX];
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    d[y][x] = '§';
                }
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

    public String getMessage() {
        return message;
    }
}
