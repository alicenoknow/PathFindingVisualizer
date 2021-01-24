package agh.oop.pathfindingVisualizer.board;

import agh.oop.pathfindingVisualizer.Controller;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

public class Tile {

    private final Coords coords;
    private TileStatus status;
    private Rectangle rectangle;
    private final int size;
    private boolean playerIsOn = false;
    private final ArrayList<IStatusChangedObserver> observers = new ArrayList<>();


    public Tile(int x, int y, int size) {
        this.size = size;
        this.coords = new Coords(x, y);
        this.status = TileStatus.EMPTY;
    }

    public void changeStatus(TileStatus newStatus) {
        this.status = newStatus;
        for (IStatusChangedObserver observer : observers)
            observer.informStatusChanged(this);
    }

    public void addObserver(IStatusChangedObserver observer) {
        this.observers.add(observer);
    }

    private Color getTileColor() {
        if (playerIsOn)
            return Colors.PLAYER;
        switch (this.status) {
            case EMPTY:
                return Colors.EMPTY;
            case WALL:
                return Colors.WALL;
            case SOURCE:
                return Colors.SOURCE;
            case DESTINATION:
                return Colors.DESTINATION;
            case VISITED:
                return Colors.VISITED;
            case PATH:
                return Colors.PATH;
        }
        return null;
    }

    public void update() {
        this.rectangle.setFill(this.getTileColor());
    }

    public void draw(Pane pane) {
        this.rectangle = new Rectangle(size, size);
        this.rectangle.setFill(this.getTileColor());
        this.rectangle.setStroke(Colors.STROKE);
        this.rectangle.setStrokeType(StrokeType.INSIDE);
        this.rectangle.setStrokeWidth(0.3);
        rectangle.setTranslateX(coords.getX());
        rectangle.setTranslateY(coords.getY());

        EventHandler<MouseEvent> eventClickedHandler = e -> {
            if (Controller.clickedSource != null && this.isEmpty()) {
                Controller.clickedSource.unhighlight();
                Controller.clickedSource.changeStatus(TileStatus.EMPTY);
                Controller.clickedSource = null;
                this.changeStatus(TileStatus.SOURCE);
            } else if (Controller.clickedDestination != null && this.isEmpty()) {
                Controller.clickedDestination.unhighlight();
                Controller.clickedDestination.changeStatus(TileStatus.EMPTY);
                Controller.clickedDestination = null;
                this.changeStatus(TileStatus.DESTINATION);
            } else
                switch (this.status) {
                    case EMPTY:
                        this.changeStatus(TileStatus.WALL);
                        break;
                    case WALL:
                        this.changeStatus(TileStatus.EMPTY);
                        break;
                    case SOURCE:
                        if (Controller.clickedSource == null) {
                            Controller.clickedSource = this;
                            this.highlight();
                        }
                        break;
                    case DESTINATION:
                        if (Controller.clickedDestination == null) {
                            Controller.clickedDestination = this;
                            this.highlight();
                        }
                        break;
                    default:
                }
        };

        EventHandler<MouseEvent> eventDragHandler = e -> {
            if (this.isEmpty())
                this.changeStatus(TileStatus.WALL);
            else if (this.isWall())
                this.changeStatus(TileStatus.EMPTY);
        };

        rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, eventClickedHandler);
        rectangle.setOnMouseDragEntered(eventDragHandler);
        pane.getChildren().add(rectangle);
    }

    private void highlight() {
        this.rectangle.setStroke(Colors.HIGHLIGHT_STROKE);
        this.rectangle.setStrokeType(StrokeType.INSIDE);
        this.rectangle.setStrokeWidth((double) size / 5);
    }

    private void unhighlight() {
        this.rectangle.setStroke(Colors.STROKE);
        this.rectangle.setStrokeType(StrokeType.INSIDE);
        this.rectangle.setStrokeWidth(0.3);
    }

    public int getX() {
        return coords.getX() / size;
    }

    public int getY() {
        return coords.getY() / size;
    }

    public boolean isWall() {
        return this.status == TileStatus.WALL;
    }

    public boolean isSource() {
        return this.status == TileStatus.SOURCE;
    }

    public boolean isDestination() {
        return this.status == TileStatus.DESTINATION;
    }

    public boolean isEmpty() {
        return this.status == TileStatus.EMPTY;
    }

    public boolean isVisited() {
        return this.status == TileStatus.VISITED;
    }

    public boolean isPath() {
        return this.status == TileStatus.PATH;
    }

    public void playerComes() {
        this.playerIsOn = true;
        update();
    }

    public void playerLeaves() {
        this.playerIsOn = false;
        update();
    }

    public boolean isPlayerOn() {
        return playerIsOn;
    }

    @Override
    public String toString() {
        return "(" + coords.getX() + ", " + coords.getY() + ") " + status;
    }

    @Override
    public int hashCode() {
        return coords.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Tile))
            return false;
        Tile that = (Tile) o;
        return (this.coords.getX() == that.coords.getX() && this.coords.getY() == that.coords.getY());
    }
}
