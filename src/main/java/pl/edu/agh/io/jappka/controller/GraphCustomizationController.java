package pl.edu.agh.io.jappka.controller;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GraphCustomizationController {

    private Stage stage;
    private AppController appController;

    @FXML
    private Button CancelButton;

    @FXML
    private Button ApplyButton;

    @FXML
    private ListView<String> BarsListView;

    @FXML
    private ColorPicker ColorPicker;

    private final ObservableList<String> appNames = FXCollections.observableArrayList();

    public void init(AppController appController) {
        // Init the apps list
        this.appController = appController;
        Set<String> currentlyDisplayedApps = this.appController.getObData().keySet();
        this.BarsListView.getItems().addAll(currentlyDisplayedApps);

        this.BarsListView.getItems().forEach(appName -> this.appNames.add(appName));
        this.BarsListView.setCellFactory(param -> new AppCell());
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private void handleApply(ActionEvent event) {
        this.appController.setAppsOrderOnGraph(Lists.reverse(new LinkedList<>(this.BarsListView.getItems())));
        this.stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleOnClick(javafx.scene.input.MouseEvent mouseEvent) {
        String appName;
        try {
            appName = BarsListView.getSelectionModel().getSelectedItem().toString();
        }
        catch (NullPointerException ex) {
            return;
        }
    }


    private class AppCell extends ListCell<String> {

        String textView = null;

        public AppCell() {

            ListCell thisCell = this;
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setAlignment(Pos.CENTER);

            setOnDragDetected(event -> {
                if (getItem() == null) {
                    return;
                }

                ObservableList<String> items = getListView().getItems();

                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem());
                dragboard.setDragView(null);
                dragboard.setContent(content);

                event.consume();
            });

            setOnDragOver(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            setOnDragEntered(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    setOpacity(0.3);
                }
            });

            setOnDragExited(event -> {
                if (event.getGestureSource() != thisCell &&
                        event.getDragboard().hasString()) {
                    setOpacity(1);
                }
            });

            setOnDragDropped(event -> {
                if (getItem() == null) {
                    return;
                }

                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString()) {
                    ObservableList<String> items = getListView().getItems();
                    int draggedIdx = items.indexOf(db.getString());
                    int thisIdx = items.indexOf(getItem());

                    String temp = appNames.get(draggedIdx);
                    appNames.set(draggedIdx, appNames.get(thisIdx));
                    appNames.set(thisIdx, temp);

                    items.set(draggedIdx, getItem());
                    items.set(thisIdx, db.getString());

                    List<String> itemscopy = new ArrayList<>(getListView().getItems());
                    getListView().getItems().setAll(itemscopy);

                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            });

            setOnDragDone(DragEvent::consume);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
            } else {
                textView = (
                        appNames.get(
                                getListView().getItems().indexOf(item)
                        )
                );
                setText(textView);
            }
        }
    }
}
