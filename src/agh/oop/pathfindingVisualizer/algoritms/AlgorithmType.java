package agh.oop.pathfindingVisualizer.algoritms;

public enum AlgorithmType {
    DFS,
    BFS,
    A_STAR;

    private String toString;

    static {
        DFS.toString = "DFS";
        BFS.toString = "BFS";
        A_STAR.toString = "A*";
    }

    @Override
    public String toString(){
        return toString;
    }
}
