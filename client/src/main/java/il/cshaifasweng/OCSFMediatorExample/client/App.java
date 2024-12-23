package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BoardStatus;
import il.cshaifasweng.OCSFMediatorExample.entities.GameOver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class App extends Application {

    private static Scene scene;
    private static Stage stage;
    private boolean flag;

    @Override
    public void start(Stage stage) throws IOException {
        EventBus.getDefault().register(this);
        flag = true;
        App.stage = stage;
        scene = new Scene(loadFXML("primary")); // Load the primary screen for IP and port entry
        stage.setScene(scene);
        stage.setTitle("XO Game");
        stage.show();
    }

    @Subscribe
    public void onBoardMessage(BoardStatus message) {
        Platform.runLater(() -> {
            if (flag) {
                flag = false;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
                    Parent root = loader.load();
                    SecondaryController controller = loader.getController();
                    controller.initializeBoard(message);
                    Scene secondScene = new Scene(root);
                    stage.setScene(secondScene);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Subscribe
    public void onGameOver(GameOver message) {
        System.out.println("Game Over");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    String.format("Game Over, %s", message.getResult()), ButtonType.OK);
            alert.showAndWait();
            try {
                scene = new Scene(loadFXML("primary"));
                stage.setScene(scene);
                stage.show();
                flag = true; // Reset the flag for the next game session
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        EventBus.getDefault().unregister(this);
        try {
            SimpleClient.getClient().sendToServer("remove player");
            System.out.println("remove player");
            SimpleClient.getClient().closeConnection();
        } catch (Exception e) {
            System.out.println("No active connection to close.");
        }
        super.stop();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
