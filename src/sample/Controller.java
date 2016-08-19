package sample;

import SOTA.SOTAtimer;
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

    private boolean right;
    private boolean left;
    private boolean jump;



    public void onKeyPressed(KeyEvent keyEvent) {
        KeyCode c = keyEvent.getCode();

        if(c == KeyCode.RIGHT || c == KeyCode.D){
            right = true;
        }
        if(c == KeyCode.LEFT || c == KeyCode.A){
            left = true;
        }
        if(c == KeyCode.UP || c == KeyCode.D || c == KeyCode.SPACE){
            jump = true;
        }


    }

    public void onKeyReleased(KeyEvent keyEvent) {
        KeyCode c = keyEvent.getCode();

        if(c == KeyCode.RIGHT || c == KeyCode.D){
            right = false;
        }
        if(c == KeyCode.LEFT || c == KeyCode.A){
            left = false;
        }
        if(c == KeyCode.UP || c == KeyCode.D || c == KeyCode.SPACE){
            jump = false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SOTAtimer handler = new SOTAtimer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String output = handler.handle(right, left, jump);
                console.setText(output);
            }
        };
        Timer t = new Timer();
        t.schedule(task, 100);
    }
}
