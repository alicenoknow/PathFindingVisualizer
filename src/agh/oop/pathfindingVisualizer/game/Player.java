package agh.oop.pathfindingVisualizer.game;

import agh.oop.pathfindingVisualizer.controller.Controller;
import agh.oop.pathfindingVisualizer.Main;
import agh.oop.pathfindingVisualizer.board.Board;
import agh.oop.pathfindingVisualizer.board.Tile;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Player {
    private Tile position;
    private Board gameBoard;
    private boolean FINISHED = false;

    public void startPlaying(Board board) {
        this.gameBoard = board;
        this.position = gameBoard.getSource();
        this.position.playerComes();

        Main.stage.addEventFilter(KeyEvent.KEY_PRESSED, keyListener);

    }

    private void move(Tile next) {
        if (next != null && !next.isWall()) {
            if (next.isDestination()) {
                FINISHED = true;
                this.position.playerLeaves();
                return;
            }
            this.position.playerLeaves();
            this.position = next;
            this.position.playerComes();
        }
    }

    public EventHandler<KeyEvent> keyListener = event -> {
        KeyCode move = event.getCode();
        switch (move) {
            case UP:
                move(gameBoard.getNeighbourN(position));
                break;
            case DOWN:
                move(gameBoard.getNeighbourS(position));
                break;
            case LEFT:
                move(gameBoard.getNeighbourW(position));
                break;
            case RIGHT:
                move(gameBoard.getNeighbourE(position));
                break;
            default:
        }
        event.consume();
    };

    public boolean finished() {
        if (FINISHED) {
            Main.stage.removeEventFilter(KeyEvent.KEY_PRESSED, keyListener);
            System.out.println("ddd");
        }
        return FINISHED;
    }
}
