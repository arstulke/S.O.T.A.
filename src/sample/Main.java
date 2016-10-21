package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

@SuppressWarnings("deprecation")
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            KeyCode key = t.getCode();
            if (key == KeyCode.ESCAPE) {
                primaryStage.close();
                System.exit(0);
            }
        });

        primaryStage.setTitle("State of the art");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
