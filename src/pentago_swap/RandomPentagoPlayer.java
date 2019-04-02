package pentago_swap;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import student_player.MyTools;
import student_player.Node;
import student_player.SimulationResult;

/**
 * @author mgrenander
 */
public class RandomPentagoPlayer extends PentagoPlayer {
    public RandomPentagoPlayer() {
        super("RandomPlayer");
    }

    public RandomPentagoPlayer(String name) {
        super(name);
    }

    @Override
    public Move chooseMove(PentagoBoardState boardState) {
        //return boardState.getRandomMove();
    	
    	Node root = new Node();
        root.bs = (PentagoBoardState) boardState.clone();
        
        Move attempt = MyTools.GetBestMove(root);

        // Return your move to be processed by the server.
        return attempt;
    }
    
}
