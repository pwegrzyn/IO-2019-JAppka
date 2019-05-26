package pl.edu.agh.io.jappka.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionsControllerHelper {

    private Stage primaryStage;
    private Scene primaryScene;
    private Scene graphScene;
    public ActionsControllerHelper(Stage primaryStage, Scene primaryScene)
    {
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
    }

    public void handleAddApplicationAction(ActionEvent event, AppController appController, String currentTheme)  {

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/addAppView.fxml"));
            AnchorPane layout = loader.load();
            AddAppController controller = loader.getController();
            controller.initialize(appController);
            Scene scene = new Scene(layout);
            Stage stage = new Stage();
            stage.setTitle("Add Application");
            stage.setScene(scene);
            scene.getStylesheets().add(currentTheme);
            controller.setStage(stage);
            stage.setTitle("Add app");
            stage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleShowCharts(ActionEvent event, AppController appController, String currentTheme,
                                 ObservableMap<String, List<AbstractActivityPeriod>> obData){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/chartView.fxml"));
            AnchorPane layout = loader.load();
            ChartController controller = loader.getController();
            controller.initialize(appController);
            controller.setData(obData);
            controller.drawGraph();
            Stage stage = new Stage();
            Scene scene = new Scene(layout);
            scene.getStylesheets().add(currentTheme);
            stage.setScene(scene);
            stage.setTitle("Chart generation");
            controller.setStage(stage);
            stage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleAddOwnEventAction(AppController appController, String currentTheme){

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/addOwnEventView.fxml"));
            AnchorPane layout = loader.load();
            AddOwnEventController controller = loader.getController();
            controller.initialize(appController);
            Scene scene = new Scene(layout);
            scene.getStylesheets().add(currentTheme);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Adding own event");
            controller.setStage(stage);
            stage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleGenerateReport(String currentTheme,ObservableMap<String, List<AbstractActivityPeriod>> data, AppController appController){

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/generateGraphView.fxml"));
            AnchorPane layout = loader.load();

            ReportGenerationController controller = loader.getController();
            controller.setData(data);
            Scene reportGenerationScene = new Scene(layout);
            Stage reportGenerationStage = new Stage();
            reportGenerationStage.setTitle("Report generation");
            reportGenerationStage.setScene(reportGenerationScene);
            reportGenerationScene.getStylesheets().add(currentTheme);
            controller.setStage(reportGenerationStage);
            controller.init(appController);
            reportGenerationStage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleSave(ActionEvent event, String currentTheme, AppController appController, boolean save){
        try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("Jappka/view/save.fxml"));
            AnchorPane layout=loader.load();
            SaveController controller=loader.getController();
            controller.initialize(appController,save);

            Scene scene=new Scene(layout);
            scene.getStylesheets().add(currentTheme);
            Stage stage=new Stage();
            if (save) stage.setTitle("Configuration saving");
            else stage.setTitle("Configuration loading");
            stage.setScene(scene);
            controller.setStage(stage);
            stage.show();
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleGraphColorPicker(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/popups/graphColorPicker.fxml"));
            GridPane colorPickerLayout = loader.load();
            Stage colorPickerStage= new Stage();
            Scene colorPickerScene = new Scene(colorPickerLayout,500,300);
            colorPickerStage.setScene(colorPickerScene);
            colorPickerStage.setTitle("Grid's color scheme");
            GraphColorPickerController colorPickerController = loader.getController();
            colorPickerController.setStage(colorPickerStage);
            colorPickerStage.show();
        }catch(IOException e){
            System.out.println("Exception occurred when loading graph color picker's FXML file, Reason: ");
            e.printStackTrace();
        }

    }
}
