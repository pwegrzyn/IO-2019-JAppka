package pl.edu.agh.io.jappka.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pl.edu.agh.io.jappka.os.NativeAccessor;
import pl.edu.agh.io.jappka.os.WindowsNativeAccessor;

import java.util.List;
import java.util.stream.Collectors;

public class AddAppController {

    private AppController appController;

    @FXML private TextField field;
    @FXML private ListView listView;
    private String appName;
    private List<String> apps;
    private NativeAccessor accessor = new WindowsNativeAccessor();

    @FXML
    public void initialize(AppController appController) {
        this.appController=appController;
        apps = accessor.getActiveProcessesNames();
        apps = apps.stream().distinct()
                .map(app -> app.replace(".exe", ""))
                .sorted()
                .collect(Collectors.toList());

        listView.getItems().setAll(apps);
    }

    @FXML
    public void handleAddButton(ActionEvent event) {
        System.out.println(appName + ".exe");                //add process name (must be with + .exe) to chart
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        appController.backToMainView();
    }

    @FXML
    public void handleOnClick(javafx.scene.input.MouseEvent mouseEvent) {
        appName = listView.getSelectionModel().getSelectedItem().toString();
        field.setText(appName);
    }

    public void handleOnTextChange(javafx.scene.input.KeyEvent keyEvent) {
        if(!keyEvent.getCode().isLetterKey() && !keyEvent.getCode().isArrowKey() && !keyEvent.getCode().equals(KeyCode.BACK_SPACE)) return;
        if (keyEvent.getCode().equals(KeyCode.DOWN)){
            listView.getFocusModel().focus(0);
            listView.requestFocus();
            return;
        }
        String textInput = "";
        String lastLetter = keyEvent.getCode().toString().toLowerCase();
        if(!keyEvent.getCode().equals(KeyCode.BACK_SPACE))
            textInput = field.getText() + lastLetter;
        else
            if(textInput.length() > 0)
                textInput = textInput.substring(0, textInput.length()-1);
        updateView(textInput);
    }

    private void updateView(String word){
        apps = accessor.getActiveProcessesNames();
        apps = apps.stream().distinct()
                .map(app -> app.replace(".exe", ""))
                .filter(app -> app.toLowerCase().startsWith(word.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
        listView.getItems().setAll(apps);
    }

    private void updateView(){
        apps = accessor.getActiveProcessesNames();
        apps = apps.stream().distinct()
                .map(app -> app.replace(".exe", ""))
                .sorted()
                .collect(Collectors.toList());
        listView.getItems().setAll(apps);
    }

    public void handleSelectApp(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            appName = listView.getFocusModel().getFocusedItem().toString();
            field.setText(appName);
        }
    }

    public void handleRefreshButton(ActionEvent event) {
        updateView();
        field.setText("");
    }
}
