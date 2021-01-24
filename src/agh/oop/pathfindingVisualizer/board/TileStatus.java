package agh.oop.pathfindingVisualizer.board;

public enum TileStatus {
    EMPTY,          // empty tile
    WALL,           // this tile is an obstacle and cannot be reached
    SOURCE,         // starting point of path searching
    DESTINATION,    // ending point of path searching
    PATH,           // tile is taken into final path
    VISITED        // tile was already visited while searching

}
