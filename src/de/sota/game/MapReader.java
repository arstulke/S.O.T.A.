package de.sota.game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SuspiciousNameCombination")
public class MapReader {

    private String[][] map;
    private int width = 0;
    private char player = 'X';
    private List<de.sota.game.Event> eventList = new ArrayList<>();

    public void readMap(String path) {
        File file = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            int height = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(":")) {
                    height++;
                }
            }
            try (BufferedReader secondBr = new BufferedReader(new FileReader(file))) {
                while ((line = secondBr.readLine()) != null) {
                    line = line.replaceAll("\t", "    ");
                    if (line.startsWith(":")) {
                        for (int k = 0; k < width; k++) {
                            try {
                                map[i][k] = Character.toString(line.charAt(k + 1));
                            } catch (Exception ignore) {
                                map[i][k] = " ";
                            }
                            map[i][k] = map[i][k] == null ? " " : map[i][k];
                        }
                        i++;
                    } else if (line.startsWith("player:")) {
                        player = line.split(":")[1].toCharArray()[0];
                    } else if (line.startsWith("-")) {
                        width = line.length() - 1;
                        map = new String[height][width];
                    } else if (line.startsWith("~")) {
                        de.sota.game.Event event = EventReader.readEvent(line);
                        if (event != null)
                            eventList.add(event);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    char getPlayer() {
        return player;
    }

    public String[][] getMap() {
        return map;
    }

    public List<de.sota.game.Event> getEventList() {
        return eventList;
    }

    Point locatePlayer() {
        int y = 0;
        int x = 0;
        for (String[] ch : map) {
            for (String c : ch) {
                if (c != null && c.equals(String.valueOf(player))) {
                    map[y][x] = " ";
                    return new Point(y, x);
                }
                x++;
            }
            x = 0;
            y++;
        }
        return null;
    }
}
