package agh.oop.pathfindingVisualizer.controller;

import agh.oop.pathfindingVisualizer.Main;
import agh.oop.pathfindingVisualizer.algoritms.AlgorithmType;
import agh.oop.pathfindingVisualizer.board.Board;
import agh.oop.pathfindingVisualizer.board.Tile;
import agh.oop.pathfindingVisualizer.search.Searching;
import agh.oop.pathfindingVisualizer.game.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class Controller {
    public static Timeline timeline;
    public static Tile clickedSource = null;
    public static Tile clickedDestination = null;
    private Board board;
    private Player player;
    private int speed;
    private EventHandler<MouseEvent> mouseDisabler = MouseEvent::consume;


    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private ChoiceBox<String> algorithmChoice;
    @FXML
    private CheckBox gameMode;
    @FXML
    private Slider speedSlider;
    @FXML
    private Label message;
    @FXML
    private CheckBox diagonal;
    @FXML
    private AnchorPane boardPane;

    @FXML
    public void initialize() {
        this.board = new Board(10, 10, 50, boardPane);
        boardPane.addEventFilter(MouseDragEvent.DRAG_DETECTED, event -> boardPane.startFullDrag());
        player = new Player();
        setTimeline();
    }


    @FXML
    private void handleStartButtonAction() {
        try {
            speed = (int) speedSlider.getValue();
            timeline.getKeyFrames().clear();
            if(player != null)
                Main.stage.removeEventFilter(KeyEvent.KEY_PRESSED, player.keyListener);

            if (gameMode.isSelected()) {
                board.prepareForMaze();
                setTimelineMaze(player);
            } else {
                AlgorithmType algorithm = getAlgorithm();
                if (algorithm == null)
                    return;
                Searching search = new Searching(board, algorithm);

                board.reload();

                if (diagonal.isSelected())
                    board.setDiagonalsTrue();
                setTimelineSearch(search);
            }
            timeline.playFromStart();

        } catch (IllegalArgumentException ex) {
            message.setText("Wrong input!");
        }
    }

    @FXML
    private void handlePauseButtonAction() {
        timeline.stop();
    }

    @FXML
    private void handleResumeButtonAction() {
        timeline.play();
    }

    @FXML
    private void handleResetButtonAction() {
        timeline.pause();
        boardPane.getChildren().clear();
        this.initialize();
        boardPane.removeEventFilter(MouseEvent.ANY, mouseDisabler);
        message.setText("");
    }

    @FXML
    private void handleSetButtonAction() {
        int widthSize = getInput(width.getText());
        int heightSize = getInput(height.getText());
        if (widthSize <= 1 || heightSize <= 1 || widthSize > 200 || heightSize > 200) return;

        boardPane.getChildren().clear();
        int size = Math.max(widthSize, heightSize);
        this.board = new Board(widthSize, heightSize, 500 / size, boardPane);

    }

    @FXML
    private void handleClearButtonAction() {
        if (timeline != null)
            timeline.pause();
        this.board.clear();
        boardPane.removeEventFilter(MouseEvent.ANY, mouseDisabler);
        message.setText("");
    }

    @FXML
    private void handleRandomButtonAction() {
        if (timeline != null)
            timeline.pause();
        this.board.clear();
        this.board.fillRandomly();
        boardPane.removeEventFilter(MouseEvent.ANY, mouseDisabler);
    }


    private int getInput(String data) {
        int value = -1;
        try {
            if (data == null) {
                throw new NumberFormatException("Empty input!");
            }
            value = Integer.parseInt(data);
            if (value <= 1 || value > 160) {
                throw new NumberFormatException("Board size should be between 2 and 160!");
            }
        } catch (NumberFormatException ex) {
            message.setText("Wrong Input!");
        }
        return value;
    }

    private AlgorithmType getAlgorithm() {
        try {
            String value = algorithmChoice.getValue();
            if (value.equals(AlgorithmType.DFS.toString()))
                return AlgorithmType.DFS;
            else if (value.equals(AlgorithmType.BFS.toString()))
                return AlgorithmType.BFS;
            else if (value.equals((AlgorithmType.A_STAR.toString())))
                return AlgorithmType.A_STAR;
        } catch (NullPointerException ex) {
            message.setText("Choose algorithm!");
        }
        return null;
    }

    private void printResult(int path) {
        if (path > 0)
            message.setText("Path found! Length: " + path);
        else
            message.setText("Path not found :c");
    }

    private void setTimeline() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        Main.stage.setOnCloseRequest(event -> timeline.stop());
    }

    private void setTimelineMaze(Player player) {
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(speed),
                actionEvent -> {                    // this will happen when timeline is running
                    board.performMazeStep();
                    board.checkMazeFlag();
                    if (timeline.getStatus() == Animation.Status.PAUSED) {
                        player.startPlaying(board);
                        setTimelineGame(player);

                    }
                }));
    }

    private void setTimelineGame(Player player) {
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(speed),
                actionEvent2 -> {                    // this will happen when timeline is running
                    if (player.finished()) {
                        message.setText("Congratulations! You have reached the destination!");
                        timeline.stop();
                    }

                }));
        timeline.playFromStart();
    }

    private void setTimelineSearch(Searching search) {
        boardPane.addEventFilter(MouseEvent.ANY, mouseDisabler);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(speed),
                actionEvent -> {                    // this will happen when timeline is running
                    search.performStep();
                    if (timeline.getStatus() == Animation.Status.PAUSED) {
                        printResult(search.getResult());
                        boardPane.removeEventFilter(MouseEvent.ANY, mouseDisabler);
                    }
                }));
    }
}
