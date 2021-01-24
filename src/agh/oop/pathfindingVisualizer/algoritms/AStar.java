package agh.oop.pathfindingVisualizer.algoritms;

import agh.oop.pathfindingVisualizer.board.Board;
import agh.oop.pathfindingVisualizer.board.Tile;
import agh.oop.pathfindingVisualizer.board.TileStatus;

import java.util.*;

public class AStar extends AbstractAlgorithm {

    private final Map<Tile, Double> dist = new HashMap<>(), estimator = new HashMap<>();
    private final Queue<Tile> queue = new PriorityQueue<>(Comparator.comparing(
            o -> estimator.getOrDefault(o, Double.MAX_VALUE)));

    public AStar(Board board, Tile source, Tile destination) {
        super(board, source, destination);
        prepare();
    }

    protected void prepare() {
        dist.put(source, (double) 0);
        estimator.put(source, this.distanceE(source, destination));
        queue.add(source);
    }

    public void performStep() {
        if (!queue.isEmpty()) {
            Tile curr = queue.poll();
            if (curr.equals(destination)) {
                FLAG = FLAGS.SOLVED;
                return;
            }
            if (!curr.isSource())
                curr.changeStatus(TileStatus.VISITED);
            for (Tile next : board.getNeighbours(curr)) {
                if (!canBePath(next)) {
                    continue;
                }
                double newDist = dist.get(curr) + 1;
                if (!queue.contains(next) || newDist < dist.getOrDefault(next, Double.MAX_VALUE)) {
                    path.put(next, curr);
                    dist.put(next, newDist);
                    estimator.put(next, newDist + distanceE(next, destination));
                    if (!queue.contains(next))
                        queue.add(next);
                }
            }
        } else
            FLAG = FLAGS.UNSOLVABLE;
    }

    // Manhattan
    private double distanceM(Tile from, Tile to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }

    // Euclidean
    private double distanceE(Tile from, Tile to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2));
    }
}
