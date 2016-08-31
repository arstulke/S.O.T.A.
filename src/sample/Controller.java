package sample;

import sota.SotaHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML
    TextArea console;

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



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final SotaHandler handler = new SotaHandler(displayPosition);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String output = handler.handle(keys);
                console.setText(output);
            }
        };
        Timer t = new Timer();
        t.schedule(task, 0, 100);
    }
}
