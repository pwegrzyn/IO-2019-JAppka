package pl.edu.agh.io.jappka.charts;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class HoveredNode extends StackPane {
    public HoveredNode(String start, String end, String title) {
        setPrefSize(15, 15);

        final Label label = createDataLabel(start, end, title);

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().add(0, label);
                toFront();
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                getChildren().clear();
            }
        });
    }

    private Label createDataLabel(String start, String end, String title) {
        final Label label = new Label("Start: " + start + "\n" + "End: " + end + "\n" + title);
        label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
        label.setStyle("-fx-font-size: 20;");

        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }
}
