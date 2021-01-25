package agh.oop.pathfindingVisualizer.search;

import agh.oop.pathfindingVisualizer.controller.Controller;
import agh.oop.pathfindingVisualizer.algoritms.*;
import agh.oop.pathfindingVisualizer.board.Board;

public class Searching {

    private int path = -1;
    private AbstractAlgorithm algorithm;

    public Searching(Board board, AlgorithmType algorithmType) {

        switch (algorithmType) {
            case BFS -> this.algorithm = new BFS(board, board.getSource(), board.getDestination());
            case DFS -> this.algorithm = new DFS(board, board.getSource(), board.getDestination());
            case A_STAR -> this.algorithm = new AStar(board, board.getSource(), board.getDestination());
        }
    }

    public void performStep() {
        if (algorithm.flag() == FLAGS.SOLVED) {
            path = algorithm.drawPath();
            Controller.timeline.pause();
        } else if (algorithm.flag() == FLAGS.UNSOLVABLE)
            Controller.timeline.pause();
        else
            algorithm.performStep();
    }

    public int getResult() {
        return path;
    }
}
