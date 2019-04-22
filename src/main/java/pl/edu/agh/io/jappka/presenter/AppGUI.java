package pl.edu.agh.io.jappka.presenter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.controller.AppController;

import java.io.IOException;

public class AppGUI {
    private Stage primaryStage;

    public AppGUI(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void initApplication() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Jappka/view/mainView.fxml"));
        BorderPane rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        AppController controller = loader.getController();
        controller.setPrimaryStageElements(primaryStage, scene);
    }
}