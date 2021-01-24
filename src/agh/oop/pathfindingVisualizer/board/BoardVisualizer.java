package agh.oop.pathfindingVisualizer.board;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class BoardVisualizer implements IStatusChangedObserver{

    private final ArrayList<ArrayList<Tile>> board;
    private final AnchorPane pane;

    public BoardVisualizer(ArrayList<ArrayList<Tile>> board, AnchorPane pane){
        this.board = board;
        this.pane = pane;
    }

    public void draw(){
        for(int i = 0; i < board.size(); i++)
            for(int j = 0; j < board.get(0).size(); j++){
                board.get(i).get(j).draw(pane);
            }
    }

    public void informStatusChanged(Tile tile){
        tile.update();
    }

}
