package pentago_swap;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import student_player.MyTools;
import student_player.Node;
import student_player.SimulationResult;
import student_player.ZeroSystem;
import student_player.ZeroSystemV2;

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
    	
    	//Node root = new Node(boardState);
        //root.bs = (PentagoBoardState) boardState.clone();
        //
        //Move attempt = MyTools.GetBestMove(root);
    	ZeroSystem ZeroSystem = new ZeroSystem(boardState);
    	Move attempt = ZeroSystem.EngageZeroSystem();
        // Return your move to be processed by the server.
        return attempt;
    }
    
}
