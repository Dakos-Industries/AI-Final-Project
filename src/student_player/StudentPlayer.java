package student_player;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("Zero System");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {        
        Node root = new Node();
        root.bs = (PentagoBoardState) boardState.clone();
        
        Move attempt = MyTools.GetBestMove(root);

        // Return your move to be processed by the server.
        return attempt;
    }
    
    private Move GetBestMove(Node current)
    {
    	ArrayList<PentagoMove> moves = current.bs.getAllLegalMoves();
    	
    	for(PentagoMove move : moves)
    	{
    		if (this.SimpleDFS(current, move))
    		{
    			return move;
    		}
    	}
    	
    	
    	return moves.get(0);
    }
    
    private Boolean SimpleDFS(Node current, PentagoMove move)
    {
    	Node next = new Node();
		next.bs = (PentagoBoardState) current.bs.clone();
		int maxPlayer = next.bs.getTurnPlayer();
		next.bs.processMove(move);
		
		if(next.bs.getWinner() == maxPlayer)
		{
			return true;
		}
		
		//simulate enemy
		Node enemy = new Node();
		enemy.bs = (PentagoBoardState) next.bs.clone();
		int minPlayer = next.bs.getTurnPlayer();
		PentagoMove move2 = (PentagoMove) enemy.bs.getRandomMove();
		enemy.bs.processMove(move2);
		
		if(next.bs.getWinner() == minPlayer)
		{
			return false;
		}
		
		//draw
		if (enemy.bs.getTurnNumber() >= 36)
		{
			return false;
		}
		
		ArrayList<PentagoMove> moves = enemy.bs.getAllLegalMoves();
    	
    	for(PentagoMove m : moves)
    	{
    		if (this.SimpleDFS(enemy, m))
    		{
    			return true;
    		}
    	}
    	
    	// no move yields victory
    	return false;
		
    }
    
}