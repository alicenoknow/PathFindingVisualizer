package agh.oop.pathfindingVisualizer.algoritms;

import agh.oop.pathfindingVisualizer.board.Board;
import agh.oop.pathfindingVisualizer.board.Tile;
import agh.oop.pathfindingVisualizer.board.TileStatus;

import java.util.*;

public class BFS extends AbstractAlgorithm {

    final Map<Tile, Integer> dist = new HashMap<>();
    final Queue<Tile> queue = new LinkedList<>();

    public BFS(Board board, Tile source, Tile destination) {
        super(board, source, destination);
        prepare();
    }

    protected void prepare() {
        queue.add(source);
        dist.put(source, 0);
        path.put(source, null);
    }

    public void performStep() {

        if (!queue.isEmpty()) {
            Tile curr = queue.poll();

            if (curr.equals(destination)) {
                FLAG = FLAGS.SOLVED;
                return;
            }
            if (curr.isEmpty())
                curr.changeStatus(TileStatus.VISITED);

            for (Tile next : board.getNeighbours(curr)) {
                if (!canBePath(next))
                    continue;
                if (!queue.contains(next))
                    queue.add(next);
                else if (dist.get(curr) + 1 >= dist.getOrDefault(next, Integer.MAX_VALUE))
                    continue;
                path.put(next, curr);
                dist.put(next, dist.get(curr) + 1);
            }
        } else
            FLAG = FLAGS.UNSOLVABLE;
    }

}
