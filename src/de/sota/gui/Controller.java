package de.sota.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import de.sota.game.GameHandler;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class Controller implements Initializable {

    @FXML
    TextArea console;

    @FXML
    TextField message;

    @FXML
    AnchorPane pane;

    private Clip clip;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            AudioInputStream sound = getAudioInputStream(new File(System.getProperty("user.dir") + "/sounds/soundtrack.wav"));
            clip = AudioSystem.getClip();
            clip.open(sound);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        final GameHandler handler = new GameHandler(displayPosition) {
            @Override
            public void onFinish() {
                try {
                    playSoundWithPause("win.wav");
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDie() {
                try {
                    clip.stop();
                    AudioInputStream sound = AudioSystem.getAudioInputStream(new File(System.getProperty("user.dir") + "/sounds/fail.wav"));
                    Clip die = AudioSystem.getClip();
                    die.open(sound);
                    die.addLineListener(event -> {
                        if (event.toString().startsWith("Stop")) {
                            clip.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    });
                    die.start();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTeleport() {
                try {
                    playSoundWithPause("teleport.wav");
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    e.printStackTrace();
                }
            }

            private void playSoundWithPause(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
                long millis = clip.getMicrosecondPosition();
                clip.stop();
                AudioInputStream sound = AudioSystem.getAudioInputStream(new File(System.getProperty("user.dir") + "/sounds/" + filename));

                Clip teleport = AudioSystem.getClip();
                teleport.open(sound);
                teleport.addLineListener(event -> {
                    if (event.toString().startsWith("Stop")) {
                        clip.setMicrosecondPosition(millis);
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                });
                teleport.start();
            }

        };

        AnchorPane.setBottomAnchor(console, 0.0);
        AnchorPane.setTopAnchor(console, 0.0);
        AnchorPane.setLeftAnchor(console, 0.0);
        AnchorPane.setRightAnchor(console, 0.0);

        AnchorPane.setTopAnchor(message, 40.0);

        pane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            AnchorPane.setLeftAnchor(message, (newSceneWidth.doubleValue() - message.getPrefWidth()) / 2);
            int fontSize = (int) (newSceneWidth.doubleValue() / 32 * 1.48148);
            console.setFont(new Font("Courier New Bold", fontSize));
        });

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateKeysBooleans();

                String output = handler.controlMovement(keyRight, keyLeft, keyUp, keyDown);
                console.setText(output);

                String msg = handler.getMessage();
                if (!message.getText().equals(msg)) {
                    if (msg != null && msg.length() > 0) {
                        message.setText(msg);
                        if (!message.isVisible())
                            message.setVisible(true);
                    } else {
                        message.setVisible(false);
                        message.setText("");
                    }
                }
            }
        };
        Timer game = new Timer();
        game.schedule(task, 0, 100);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private boolean[] keys = new boolean[9];


    public void onKeyPressed(KeyEvent keyEvent) {
        updateKeyBooleans(true, keyEvent.getCode());
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        updateKeyBooleans(false, keyEvent.getCode());
    }

    private void updateKeyBooleans(boolean b, KeyCode c) {
        if (c == KeyCode.RIGHT)
            keys[0] = b;

        if (c == KeyCode.D) {
            keys[1] = b;
        }


        if (c == KeyCode.LEFT) {
            keys[2] = b;
        }
        if (c == KeyCode.A) {
            keys[3] = b;
        }


        if (c == KeyCode.UP) {
            keys[4] = b;
        }
        if (c == KeyCode.W) {
            keys[5] = b;
        }
        if (c == KeyCode.SPACE) {
            keys[6] = b;
        }

        if (c == KeyCode.DOWN) {
            keys[7] = b;
        }
        if (c == KeyCode.S) {
            keys[8] = b;
        }
    }


    //region Important
    //Point displayPosition = new Point(6, 1);
    private Point displayPosition = new Point(5, 15);
    //endregion

    private boolean keyRight;
    private boolean keyLeft;
    private boolean keyUp;
    private boolean keyDown;

    private void updateKeysBooleans() {
        keyRight = keys[0] || keys[1];
        keyLeft = keys[2] || keys[3];
        keyUp = keys[4] || keys[5] || keys[6];
        keyDown = keys[7] || keys[8];
    }
}
