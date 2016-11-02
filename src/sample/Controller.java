package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import sota.SotaHandler;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML
    TextArea console;

    @FXML
    TextField message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final SotaHandler handler = new SotaHandler(displayPosition);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateKeysBooleans();

                String output = handler.controlMovement(keyRight, keyLeft, keyUp, keyDown);
                console.setText(output);

                String msg = handler.getMessage();
                if (!message.getText().equals(msg)) {
                    if (msg != null) {
                        message.setText(msg);
                        if (!message.isVisible())
                            message.setVisible(true);
                    } else {
                        message.setVisible(false);
                    }
                }
            }
        };
        Timer t = new Timer();
        t.schedule(task, 0, 100);
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
