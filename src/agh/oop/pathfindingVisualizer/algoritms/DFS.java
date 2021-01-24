package agh.oop.pathfindingVisualizer.algoritms;

import agh.oop.pathfindingVisualizer.board.Board;
import agh.oop.pathfindingVisualizer.board.Tile;
import agh.oop.pathfindingVisualizer.board.TileStatus;

import java.util.*;

public class DFS extends AbstractAlgorithm {

    private final Stack<Tile> stack = new Stack<>();
    private final Map<Tile, Integer> dist = new HashMap<>();

    public DFS(Board board, Tile source, Tile destination) {
        super(board, source, destination);
        this.prepare();
    }

    protected void prepare() {
        stack.push(source);
        dist.put(source, 0);
        path.put(source, null);
    }

    public void performStep() {

        if (!stack.isEmpty()) {
            Tile curr = stack.pop();

            if (curr.equals(destination)) {
                FLAG = FLAGS.SOLVED;
                return;
            }
            if (curr.isEmpty())
                curr.changeStatus(TileStatus.VISITED);

            for (Tile next : board.getNeighbours(curr)) {
                if (!canBePath(next))
                    continue;
                if (!stack.contains(next))
                    stack.push(next);
                else if (dist.get(curr) + 1 >= dist.getOrDefault(next, Integer.MAX_VALUE))
                    continue;
                path.put(next, curr);
                dist.put(next, dist.get(curr) + 1);
            }
        } else
            FLAG = FLAGS.UNSOLVABLE;
    }

}
