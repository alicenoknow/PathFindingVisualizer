package agh.oop.pathfindingVisualizer.board;

import agh.oop.pathfindingVisualizer.Controller;
import javafx.scene.layout.AnchorPane;

import java.util.*;

public class Board implements IStatusChangedObserver {
    private final int width, height;
    private final ArrayList<ArrayList<Tile>> board;
    private Tile source, destination, curr, next;
    private HashSet<Tile> walls;
    private final Random generator = new Random();
    private boolean MAZE_FLAG = false;
    private boolean diagonals = false;


    public Board(int width, int height, int size, AnchorPane pane) {
        this.width = width;
        this.height = height;
        this.board = new ArrayList<>();
        BoardVisualizer visualizer = new BoardVisualizer(board, pane);
        for (int h = 0; h < height; h++) {
            board.add(new ArrayList<>());
            for (int w = 0; w < width; w++) {
                board.get(h).add(new Tile(w * size, h * size, size));
                board.get(h).get(w).addObserver(visualizer);
                board.get(h).get(w).addObserver(this);
            }
        }
        visualizer.draw();
        setDefaultSourceAndDestination();
    }

    public void fillRandomly() {
        prepareForMaze();
        while (!MAZE_FLAG)
            performMazeStep();
        MAZE_FLAG = false;
        reload();
    }

    public void clear() {
        for (int h = 0; h < height; h++)
            for (int w = 0; w < width; w++)
                board.get(h).get(w).changeStatus(TileStatus.EMPTY);
        setDefaultSourceAndDestination();
    }

    private void setDefaultSourceAndDestination() {
        board.get(0).get(0).changeStatus(TileStatus.SOURCE);
        board.get(height - 1).get(width - 1).changeStatus(TileStatus.DESTINATION);
    }

    public ArrayList<Tile> getNeighbours(Tile curr) {
        if (diagonals)
            return getNeighboursWithDiagonals(curr);
        return getNeighboursNonDiagonals(curr);
    }

    public ArrayList<Tile> getNeighboursWithDiagonals(Tile curr) {
        ArrayList<Tile> neighbours = getNeighboursNonDiagonals(curr);
        addDiagonalNeighbours(curr, neighbours);
        return neighbours;
    }


    public ArrayList<Tile> getNeighboursNonDiagonals(Tile curr) {
        ArrayList<Tile> neighbours = new ArrayList<>();
        neighbours.add(getNeighbourN(curr));
        neighbours.add(getNeighbourE(curr));
        neighbours.add(getNeighbourS(curr));
        neighbours.add(getNeighbourW(curr));
        neighbours.removeAll(Collections.singleton(null));
        return neighbours;
    }

    public Tile getSource() {
        return source;
    }

    public Tile getDestination() {
        return destination;
    }

    public void reload() {
        diagonals = false;
        for (int h = 0; h < height; h++)
            for (int w = 0; w < width; w++) {
                if (board.get(h).get(w).isVisited() || board.get(h).get(w).isPath())
                    board.get(h).get(w).changeStatus(TileStatus.EMPTY);
                if (board.get(h).get(w).isPlayerOn())
                    board.get(h).get(w).playerLeaves();
            }
    }


    public void setDiagonalsTrue() {
        this.diagonals = true;
    }

    public void prepareForMaze() {
        for (int h = 0; h < height; h++)
            for (int w = 0; w < width; w++)
                if (!board.get(h).get(w).isSource() && !board.get(h).get(w).isDestination())
                    board.get(h).get(w).changeStatus(TileStatus.WALL);
        this.walls = new HashSet<>();
        addWalls(source);
    }

    public void checkMazeFlag() {
        if (MAZE_FLAG) {
            Controller.timeline.pause();
            MAZE_FLAG = false;
        }
    }

    public void performMazeStep() {
        if (!walls.isEmpty()) {
            curr = getRandomWall();
            next = getRandomNext();
            if (next != null)
                connect(curr, next);
            addWalls(curr);
            walls.remove(curr);
        } else if (!isConnected(destination)) {
            next = getNeighbourN(destination);
            if (checkAccessToNext()) {
                next.changeStatus(TileStatus.EMPTY);
                return;
            }
            next = getNeighbourE(destination);
            if (checkAccessToNext()) {
                next.changeStatus(TileStatus.EMPTY);
                return;
            }
            next = getNeighbourS(destination);
            if (checkAccessToNext()) {
                next.changeStatus(TileStatus.EMPTY);
                return;
            }
            next = getNeighbourW(destination);
            if (checkAccessToNext()) {
                next.changeStatus(TileStatus.EMPTY);
            }
        } else
            MAZE_FLAG = true;
    }

    private Tile getRandomWall() {
        int idx = generator.nextInt(walls.size());
        Tile random = null;
        Iterator<Tile> it = walls.iterator();
        int counter = 0;
        while (it.hasNext()) {
            random = it.next();
            if (counter == idx)
                break;
            counter++;
        }
        return random;
    }

    @Override
    public void informStatusChanged(Tile tile) {
        if (tile.isSource())
            this.source = tile;
        else if (tile.isDestination())
            this.destination = tile;
    }

    public Tile getNeighbourN(Tile curr) {
        if (curr != null)
            if (curr.getY() - 1 >= 0)
                return board.get(curr.getY() - 1).get(curr.getX());
        return null;
    }

    public Tile getNeighbourE(Tile curr) {
        if (curr != null)
            if (curr.getX() + 1 < width)
                return board.get(curr.getY()).get(curr.getX() + 1);
        return null;
    }

    public Tile getNeighbourS(Tile curr) {
        if (curr != null)
            if (curr.getY() + 1 < height)
                return board.get(curr.getY() + 1).get(curr.getX());
        return null;
    }

    public Tile getNeighbourW(Tile curr) {
        if (curr != null)
            if (curr.getX() - 1 >= 0)
                return board.get(curr.getY()).get(curr.getX() - 1);
        return null;
    }

    private Tile getNextNeighbourN(Tile curr) {
        return getNeighbourN(getNeighbourN(curr));
    }

    private Tile getNextNeighbourE(Tile curr) {
        return getNeighbourE(getNeighbourE(curr));
    }

    private Tile getNextNeighbourS(Tile curr) {
        return getNeighbourS(getNeighbourS(curr));
    }

    private Tile getNextNeighbourW(Tile curr) {
        return getNeighbourW(getNeighbourW(curr));
    }

    private void addDiagonalNeighbours(Tile curr, ArrayList<Tile> neighbours) {
        int x = curr.getX();
        int y = curr.getY();
        if (x - 1 >= 0) {
            if (y - 1 >= 0)
                neighbours.add(board.get(y - 1).get(x - 1));
            if (y + 1 < height)
                neighbours.add(board.get(y + 1).get(x - 1));
        }
        if (x + 1 < width) {
            if (y - 1 >= 0)
                neighbours.add(board.get(y - 1).get(x + 1));
            if (y + 1 < height)
                neighbours.add(board.get(y + 1).get(x + 1));
        }
    }


    private void addWalls(Tile curr) {
        next = getNextNeighbourN(curr);
        if (checkAccessToNext())
            walls.add(next);

        next = getNextNeighbourE(curr);
        if (checkAccessToNext())
            walls.add(next);

        next = getNextNeighbourS(curr);
        if (checkAccessToNext())
            walls.add(next);

        next = getNextNeighbourW(curr);
        if (checkAccessToNext())
            walls.add(next);
    }

    private Tile getRandomNext() {
        ArrayList<Tile> tiles = new ArrayList<>();
        next = getNextNeighbourN(curr);
        if (checkAccessToNext())
            tiles.add(next);

        next = getNextNeighbourE(curr);
        if (checkAccessToNext())
            tiles.add(next);

        next = getNextNeighbourS(curr);
        if (checkAccessToNext())
            tiles.add(next);

        next = getNextNeighbourW(curr);
        if (checkAccessToNext())
            tiles.add(next);

        if (tiles.size() > 0) {
            Collections.shuffle(tiles);
            return tiles.get(0);
        }
        return null;
    }

    private void connect(Tile first, Tile second) {
        int x = first.getX() - (first.getX() - second.getX()) / 2;
        int y = first.getY() - (first.getY() - second.getY()) / 2;
        Tile between = board.get(y).get(x);
        if (between.isWall())
            between.changeStatus(TileStatus.EMPTY);
        if (first.isWall())
            first.changeStatus(TileStatus.EMPTY);
    }

    private boolean isConnected(Tile tile) {
        next = getNeighbourN(tile);
        if (checkAccessToNext())
            return true;
        next = getNeighbourE(tile);
        if (checkAccessToNext())
            return true;
        next = getNeighbourS(tile);
        if (checkAccessToNext())
            return true;
        next = getNeighbourW(tile);
        return checkAccessToNext();
    }

    private boolean checkAccessToNext() {
        return next != null && !next.isWall();
    }
}
