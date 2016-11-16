package de.sota.game;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.List;

public abstract class GameHandler {

    private boolean keyRight; //is *Key is pressed
    private boolean keyLeft; //is *Key is pressed
    private boolean keyUp; //is *Key is pressed
    private boolean keyDown; //is *Key is pressed

    private boolean isJumping = false; //is Jumping
    private int jumpTick = 0; //UP UP STAY DOWN DOWN
    private boolean isAutoJumping = false; //small jump 1 step
    private int autoJumpTick = 0; //UP LEFT/RIGHT
    private int autoJumpDir = 0; //left -1      right 1
    private int ladderTick = 0; //LADDER TICKS

    private char[][] map; //charMap
    private List<de.sota.game.Event> eventList;
    private char player; //Player Character
    public Point position; //Player Position

    private Point displayPosition;

    //Event fields
    private Point checkpoint = new Point();
    private String message = "";
    private int messageTicks = -1;

    public GameHandler(Point displayPos) {
        loadMap(System.getProperty("user.dir") + "\\map.txt");
        displayPosition = new Point(0, 0);
        displayPosition.setLocation(displayPos);
    }

    public GameHandler(Point displayPos, String path) {
        loadMap(path);

        displayPosition = new Point(0, 0);
        displayPosition.setLocation(displayPos);
    }

    private void loadMap(String path) {
        MapReader mr = new MapReader();
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

        checkDie();
        checkMovement();
        checkEvents();

        return display();
    }

    private void checkDie() {
        if (map[position.x + 1][position.y] == '^') {
            die();
        }
    }

    private void die() {
        for (de.sota.game.Event event : eventList) {
            event.setTriggerable(event.getEventype() != de.sota.game.Event.EventType.CHECKPOINT || !event.getParams().get("checkpoint").equals(checkpoint));
        }
        position.setLocation(checkpoint);

        onDie();
    }

    private void checkMovement() {
        try {
            boolean isOnLadder = map[position.x][position.y] == '#';
            boolean isOnGround = !isPassableChar(map[position.x + 1][position.y], true);

            //region GravityMovement
            if (!isOnGround && !isJumping && !isAutoJumping && !isOnLadder) {
                move(1, 0, true); //move 1 down
            }
            isOnGround = !isPassableChar(map[position.x + 1][position.y], true);
            //endregion

            //region Left/Right Movement
            if (keyRight && !keyLeft) {
                move(0, 1, false); //move right
            } else if (!keyRight && keyLeft) {
                move(0, -1, false); //move left
            }
            //endregion

            //region jumping
            if (isAutoJumping) {
                autoJump();
            } else if (isJumping) {
                jump();
            }
            //endregion

            //region jump Movement
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
            //endregion

            //region Ladder Movement (Jump, Right, Left)
            int ladderSpeed = 1;
            if (keyUp && !keyDown && isOnLadder && !((keyRight || keyLeft) && !(keyRight && keyLeft))) {
                if (ladderTick == 0) {
                    move(-1, 0, false); //move up
                    ladderTick = ladderSpeed;
                } else {
                    ladderTick--;
                }
            }
            if (keyUp && !keyDown && isOnLadder && keyRight) {
                isJumping = true; //jump from ladder to right
            }
            if (keyUp && !keyDown && isOnLadder && keyLeft) {
                isJumping = true; //jump from ladder to left
            }
            if (keyDown && !keyUp && (isOnLadder || moveToDefinedChar(1, 0, '#'))) {
                if (ladderTick == 0) {
                    move(1, 0, false); //move up
                    ladderTick = ladderSpeed;
                } else {
                    ladderTick--;
                }
            }
            //endregion
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

    private static boolean isPassableChar(char s, boolean gravityCheck) {
        char[] validChars = " abcdefghijklmnopqrstuvwxyz*".toCharArray();
        char[] ladder = "#".toCharArray();
        for (char a : validChars) {
            if (s == a) {
                return true;
            }
        }
        if (!gravityCheck) {
            for(char c : ladder){
                if (c == s) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkEvents() {
        for (de.sota.game.Event event : eventList) {
            boolean execute = event.shouldTriggered(position) && event.isTriggerable();
            if (execute) {
                switch (event.getEventype()) {
                    case TELEPORT:
                        Point teleportTarget = (Point) event.getParams().get("teleportTarget");
                        char targetChar = map[teleportTarget.x][teleportTarget.y];
                        if (GameHandler.isPassableChar(targetChar, true))
                            position.setLocation(teleportTarget);
                        onTeleport();
                        break;

                    case DISPLAY:
                        event.setTriggerable(false);
                        message = (String) event.getParams().get("msg");
                        messageTicks = (int) event.getParams().get("time");
                        break;

                    case CHECKPOINT:
                        Point newCheckpoint = (Point) event.getParams().get("checkpoint");
                        checkpoint.setLocation(newCheckpoint);
                        event.setTriggerable(false);
                        break;

                    case END:
                        String param = (String) event.getParams().get("end");
                        if(StringUtils.equalsIgnoreCase(param, "finish")) {
                            onFinish();
                            event.setTriggerable(false);
                        }
                        else if(StringUtils.equalsIgnoreCase(param, "die")) {
                            onDie();
                        }
                        break;
                }
            }
        }

        if (messageTicks == 0) {
            message = null;
            messageTicks = -1;
        } else if (messageTicks > 0) {
            messageTicks--;
        }

        eventList.stream().filter(event -> event.getEventype() == de.sota.game.Event.EventType.CHECKPOINT && !event.shouldTriggered(checkpoint)).forEach(event -> event.setTriggerable(true));
    }

    public abstract void onFinish();

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

    public abstract void onDie();

    public abstract void onTeleport();

}
