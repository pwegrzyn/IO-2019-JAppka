package pl.edu.agh.io.jappka.charts;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import pl.edu.agh.io.jappka.controller.AppController;
import pl.edu.agh.io.jappka.util.Utils;

public class HoveredNode extends StackPane {
    private AppController controller;
    private long startTime, endTime;
    private String title;

    public HoveredNode(long start, long end, String title, AppController controller) {
        this.controller = controller;
        setPrefSize(15, 15);

        startTime = start;
        endTime = end;

        String s = Utils.millisecondsToCustomStrDate(start, "HH:mm:ss");
        String e = Utils.millisecondsToCustomStrDate(end, "HH:mm:ss");

        final Label label = createDataLabel(s, e, title);

        ContextMenu menu = new ContextMenu();
        MenuItem item = new MenuItem("Remove event");
        item.setOnAction(event -> {
            controller.removePeriod(startTime, endTime, title);
        });

        setOnContextMenuRequested(event -> {
            menu.show(this.getParent(), event.getScreenX(), event.getScreenY());
        });

        setOnMouseEntered(event -> {
            getChildren().add(0, label);
            toFront();
        });

        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().clear();
            }
        });

        menu.getItems().addAll(item);
    }

    private Label createDataLabel(String start, String end, String title) {
        final Label label = new Label("Start: " + start + "\n" + "End: " + end + "\n" + title);
        label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
        label.setStyle("-fx-font-size: 20;");

        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }
}
