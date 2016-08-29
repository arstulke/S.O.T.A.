package sample;

import sota.SotaHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML
    TextArea console;

    private boolean[] keys = new boolean[7];


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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SotaHandler handler = new SotaHandler();
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
