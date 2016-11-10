package bgoug.live;

import bgoug.live.model.Cell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class JavaFXController implements Initializable {

    @FXML
    TextField txtGridWidth;

    @FXML
    TextField txtGridHeight;

    @FXML
    TextField txtIterations;

    @FXML
    GridPane cellGrid;

    @FXML
    CheckBox chkParallelStreams;

    @FXML
    ProgressBar iterationsProgress;

    @FXML
    Button startButton;

    @FXML
    Button nextIterationButton;

    @FXML
    Button runAllButton;

    @FXML
    Button cancelButton;

    @FXML
    CheckBox chkUpdateUI;

    @FXML
    Label lblSize;

    @FXML
    BorderPane borderPane;

    private SimpleBooleanProperty validInput = new SimpleBooleanProperty(true);
    private int iterations;

    // SimpleIntegerProperty currentIteration = new SimpleIntegerProperty(0);

    private SimpleObjectProperty<Task> gameTaskProperty = new SimpleObjectProperty<>();

    private LiveGame game;

    private ChangeListener<Boolean> numericValidator(TextField textField, int min, int max) {
        return (arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (checkNumericValue(min, max, textField) != -1) {
                    textField.setStyle("");
                } else {
                    textField.setStyle("-fx-border-color: darkred");
                }
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validInput.bind(Bindings.createBooleanBinding(() ->
                        txtGridWidth.styleProperty().get().length() == 0 &&
                                txtGridHeight.styleProperty().get().length() == 0 &&
                                txtIterations.styleProperty().get().length() == 0,
                txtGridWidth.styleProperty(), txtGridHeight.styleProperty(), txtIterations.styleProperty()));

        txtGridWidth.focusedProperty().addListener(numericValidator(txtGridWidth, 3, 200));
        txtGridHeight.focusedProperty().addListener(numericValidator(txtGridHeight, 3, 180));
        txtIterations.focusedProperty().addListener(numericValidator(txtIterations, 3, 1000));

        BooleanBinding taskStarted = Bindings.createBooleanBinding(() -> gameTaskProperty.get() == null, gameTaskProperty);
        startButton.disableProperty()
                   .bind(validInput.not()
                                   .and(taskStarted));

        runAllButton.disableProperty().bind(taskStarted);
        nextIterationButton.disableProperty().bind(taskStarted);
        cancelButton.disableProperty().bind(taskStarted);

        ChangeListener<Number> dimensionsListener = (arg0, oldValue, newValue) -> {
            int width, height;
            try {
                width = getTextFieldIntValue(txtGridWidth);
                height = getTextFieldIntValue(txtGridHeight);
            } catch (NumberFormatException nfe) {
                return;
            }
            double cellWidth = borderPane.getWidth() / width;
            double cellHeight = (borderPane.getHeight() - 37 - 28) / height;

            for (Node node : cellGrid.getChildren()) {
                if (!(node instanceof Rectangle)) {
                    continue;
                }

                Rectangle r = (Rectangle) node;
                r.setWidth(cellWidth);
                r.setHeight(cellHeight);
            }
        };

        cellGrid.widthProperty().addListener(dimensionsListener);
        cellGrid.heightProperty().addListener(dimensionsListener);

        lblSize.textProperty()
               .bind(Bindings.createStringBinding(() -> String.format("%1$g, %2$g", cellGrid.getWidth(), cellGrid.getHeight()), cellGrid
                       .widthProperty(), cellGrid.heightProperty()));
    }

    private int checkNumericValue(int min, int max, TextField textField) {
        try {
            Integer value = getTextFieldIntValue(textField);
            return value >= min && value <= max ? value : -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private Integer getTextFieldIntValue(TextField textField) {
        return Integer.parseInt(textField.getText());
    }

    private boolean updateUI = true;

    private boolean fastRun = false;

    private class JavaFXCell extends Cell {

        JavaFXCell(int x, int y, double width, double height) {
            super(x, y);

            Rectangle cell = new Rectangle(width, height);
            cell.fillProperty()
                .bind(Bindings.createObjectBinding(() -> liveProperty.get() ? Color.GREEN : Color.BLACK, liveProperty));
            cellGrid.add(cell, x, y);
        }

        SimpleBooleanProperty liveProperty = new SimpleBooleanProperty();

        @Override
        public void setLive(boolean live) {
            if (updateUI) {
                Platform.runLater(() -> liveProperty.set(live));
            }
            super.setLive(live);
        }
    }

    public void start() {
        cellGrid.getColumnConstraints().clear();
        cellGrid.getRowConstraints().clear();
        cellGrid.getChildren().clear();

        int width = getTextFieldIntValue(txtGridWidth);
        int height = getTextFieldIntValue(txtGridHeight);
        iterations = getTextFieldIntValue(txtIterations);

        game = new LiveGame(width, height, chkParallelStreams.isSelected());

        double cellWidth = cellGrid.getWidth() / width;
        double cellHeight = cellGrid.getHeight() / height;

        game.initialize((x, y) -> new JavaFXCell(x, y, cellWidth, cellHeight));
        final Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                int progress = 0;
                while (progress < iterations) {
                    if (!fastRun) {
                        synchronized (this) {
                            this.wait();
                        }
                    }
                    if (isCancelled()) {
                        break;
                    }

                    if (!updateUI) {
                        updateUI = progress == iterations - 1;
                    }
                    game.nextIteration();
                    updateProgress(++progress, iterations);
                }

                return null;
            }
        };

        iterationsProgress.progressProperty().bind(task.progressProperty());

        javafx.event.EventHandler<Event> handler = (e) -> {
            if (task.getException() != null) {
                task.getException().printStackTrace();
            }
            iterationsProgress.progressProperty().unbind();
            gameTaskProperty.set(null);
        };
        task.setOnSucceeded(handler);
        task.setOnFailed(handler);
        task.setOnCancelled(handler);
        gameTaskProperty.set(task);
        fastRun = false;
        updateUI = true;
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public void nextIteration() {
        notifyTask(gameTaskProperty.get());
    }

    private void notifyTask(Task task) {
        synchronized (task) {
            task.notify();
        }
    }

    public void runAllIterations() {
        updateUI = chkUpdateUI.isSelected();
        fastRun = true;
        nextIteration();
    }

    public void cancel() {
        Task task = gameTaskProperty.get();
        task.cancel(false);
        notifyTask(task);
    }
}
