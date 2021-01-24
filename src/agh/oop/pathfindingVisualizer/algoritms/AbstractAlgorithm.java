package agh.oop.pathfindingVisualizer.algoritms;

import agh.oop.pathfindingVisualizer.board.Board;
import agh.oop.pathfindingVisualizer.board.Tile;
import agh.oop.pathfindingVisualizer.board.TileStatus;

import java.util.*;

public abstract class AbstractAlgorithm {
    protected final Board board;
    protected final Tile source, destination;
    protected final Map<Tile, Tile> path = new HashMap<>();
    protected FLAGS FLAG = FLAGS.IN_PROGRESS;

    public AbstractAlgorithm(Board board, Tile source, Tile destination) {
        this.board = board;
        this.source = source;
        this.destination = destination;
    }

    public void performStep() {
    }

    public FLAGS flag() {
        return FLAG;
    }

    public int drawPath() {
        int counter = 1;
        Tile curr = path.get(destination);
        Tile next;
        while ((next = path.get(curr)) != null) {
            counter++;
            curr.changeStatus(TileStatus.PATH);
            curr = next;
        }
        return counter;
    }

    protected boolean canBePath(Tile tile) {
        return (!tile.isVisited() && !tile.isSource() && !tile.isWall());
    }
}
