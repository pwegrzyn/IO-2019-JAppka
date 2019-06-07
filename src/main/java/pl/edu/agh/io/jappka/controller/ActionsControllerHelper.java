package pl.edu.agh.io.jappka.controller;

import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.edu.agh.io.jappka.activity.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

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
            setIcon(stage);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
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
            setIcon(stage);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
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
            setIcon(stage);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
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
            setIcon(reportGenerationStage);
            reportGenerationStage.setAlwaysOnTop(true);
            reportGenerationStage.setResizable(false);
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
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/save.fxml"));
            AnchorPane layout=loader.load();
            SaveController controller=loader.getController();
            controller.initialize(appController,save);
            Scene scene=new Scene(layout);
            scene.getStylesheets().add(currentTheme);
            Stage stage=new Stage();
            setIcon(stage);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
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

    public void handleGraphCustomization(ActionEvent event, String currentTheme, AppController appController){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/graphCustomization.fxml"));
            AnchorPane customizerLayout = loader.load();
            Stage customizerStage= new Stage();
            customizerStage.setAlwaysOnTop(true);
            customizerStage.setResizable(false);
            Scene customizerScene = new Scene(customizerLayout,543,482);
            customizerScene.getStylesheets().add(currentTheme);
            customizerStage.setScene(customizerScene);
            setIcon(customizerStage);
            customizerStage.setTitle("Graph customization");
            GraphCustomizationController customizationController = loader.getController();
            customizationController.setStage(customizerStage);
            customizationController.init(appController);
            customizerStage.show();
        }catch(Exception e){
            System.out.println("Exception occurred when loading graph customizer's FXML file, Reason: ");
            e.printStackTrace();
        }

    }

    public void handleLoadAutomatically(AppController appController, String configFileLocation){
        try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("JAppka/view/save.fxml"));
            AnchorPane layout=loader.load();
            SaveController controller=loader.getController();
            controller.initialize(appController,false);
            Scene scene=new Scene(layout);
            Stage stage=new Stage();
            stage.setScene(scene);
            controller.setStage(stage);
            controller.loadAutomatically(configFileLocation);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setIcon(Stage stage) throws IOException {
        java.awt.image.BufferedImage imageIcon = ImageIO.read(getClass().getClassLoader().getResource("image/icon2.png"));
        stage.getIcons().add(SwingFXUtils.toFXImage(imageIcon, null));
    }
}
