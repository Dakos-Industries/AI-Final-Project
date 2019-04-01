package pentago_swap;

import java.util.ArrayList;
import java.util.Collections;

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
    	Node node = new Node();
    	node.CurrentState = (PentagoBoardState) boardState.clone();
    	return GetBestMove(node);
    }
    
    public static Move GetBestMove(Node current)
    {
    	ArrayList<PentagoMove> moves = current.CurrentState.getAllLegalMoves();	
    	PentagoMove candidateMove = moves.get(0);
    	double bestRatio = 0;
    	int attempt = 0;
    	boolean cont = false;
    	 	
    	for(PentagoMove move : moves)
    	{
    		cont = false;
    		// Check if enemy can win in the next move if we do this move
    		PentagoBoardState tmpBoard = (PentagoBoardState) current.CurrentState.clone();
    	    tmpBoard.processMove(move);
    	    ArrayList<PentagoMove> enemyMoves = tmpBoard.getAllLegalMoves();	
    	    if (enemyMoves.size() > 0)
    	    {
	    	    for(PentagoMove enemyMove : enemyMoves)
	    	    {
	    	    	PentagoBoardState simBoard = (PentagoBoardState) tmpBoard.clone();
	    	    	simBoard.processMove(enemyMove);
		    	    // enemy is the opponent
		    	    if (simBoard.getWinner() == simBoard.getOpponent())
		    	    {
		    	    	cont = true;
		    	    	break;
		    	    }
	    	    }    
    	    }
    	    
    	    if(tmpBoard.getWinner() == current.CurrentState.getTurnPlayer())
    	    {
    	    	candidateMove = move;
    	    	System.out.println("I win " + current.CurrentState.getTurnNumber());
    	    	break;
    	    }
    	    
    	    if (cont)
    	    {
    	    	System.out.println("Skipping attempt: " + attempt);
    	    	attempt++;
    	    	continue;
    	    }
    	    
    		attempt++;
    		SimulationResult result = CountingDFS(current, move, new SimulationResult());
    		if (result.GetWinLossRatio() > bestRatio)
    		{
    			System.out.println("Move = " + attempt);
    			bestRatio = result.GetWinLossRatio();
    			candidateMove = move;
    		}
    		//System.out.println("Attemp " + attempt + "| Ratio " + result.GetWinLossRatio() 
    		//+ "|Games played " + (result.Losses + result.Wins + result.Draws));
    	}
    	return candidateMove;
    }
    
    private static SimulationResult CountingDFS(Node current, PentagoMove move, SimulationResult result)
    {
		if(result.GetTotalTrials() >= 175)
		{
			result.ContinueSim = false;
			return result;
		}
		
    	Node next = new Node();
		next.CurrentState = (PentagoBoardState) current.CurrentState.clone();
		int maxPlayer = next.CurrentState.getTurnPlayer();
		next.CurrentState.processMove(move);
		
		int minPlayer = next.CurrentState.getTurnPlayer();
		if(UpdateResults(maxPlayer, minPlayer, next.CurrentState, result))
		{
			return result;
		}
		
		//simulate enemy
		Node enemy = new Node();
		enemy.CurrentState = (PentagoBoardState) next.CurrentState.clone();
		Move move2 = enemy.CurrentState.getRandomMove();
		enemy.CurrentState.processMove((PentagoMove)move2);

		if(UpdateResults(maxPlayer, minPlayer, enemy.CurrentState, result))
		{
			return result;
		}
		
		ArrayList<PentagoMove> moves = enemy.CurrentState.getAllLegalMoves();
    	
		// add some randomness
		Collections.shuffle(moves);
		
    	for(PentagoMove pentagoMove : moves)
    	{	
    		SimulationResult simulationResult = CountingDFS(enemy, pentagoMove, result);
    		if(!simulationResult.ContinueSim)
    		{
    			return result;
    		}
    	}
		return result;
    }
    
    /**
     * Return true is results were updated; false otherwise
     * 
     * */
    private static Boolean UpdateResults(int maxPlayer, int minPlayer, PentagoBoardState pbs, SimulationResult result)
    {
    	int winner = pbs.getWinner();
    	if(winner == maxPlayer)
		{
			result.Wins++;
			return true;
		} 
		else if (winner == minPlayer)
		{
			result.Losses++;
			return true;
		}
		else if (winner == Board.DRAW) {
			result.Draws++;
			return true;
		}
    	return false;
    }
}
