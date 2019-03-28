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
        
        Move attempt = this.GetBestMove(root);

        // Return your move to be processed by the server.
        return attempt;
    }
    
    public Move GetBestMove(Node current)
    {
    	ArrayList<PentagoMove> moves = current.bs.getAllLegalMoves();
    	
    	PentagoMove candidateMove = moves.get(0);
    	double bestRatio = 0;
    	int attempt = 0;
    	for(PentagoMove move : moves)
    	{
    		attempt++;
    		SimulationResult result = CountingDFS(current, move, new SimulationResult());
    		if (result.GetHeuristicScore() > bestRatio)
    		{
    			bestRatio = result.GetHeuristicScore();
    			candidateMove = move;
    		}
    	}   	
    	return candidateMove;
    }
    
    private SimulationResult CountingDFS(Node current, PentagoMove move, SimulationResult result)
    {
		if(result.GetTotalTrials() >= 175)
		{
			result.ContinueSim = false;
			return result;
		}
		
    	Node next = new Node();
		next.bs = (PentagoBoardState) current.bs.clone();
		int maxPlayer = next.bs.getTurnPlayer();
		next.bs.processMove(move);
		
		int minPlayer = next.bs.getTurnPlayer();
		if(next.bs.getWinner() == maxPlayer)
		{
			result.Wins++;
			return result;
		} 
		else if (next.bs.getWinner() == minPlayer)
		{
			result.Losses++;
			return result;
		}
		else if (next.bs.getWinner() == Board.DRAW) {
			result.Draws++;
			return result;
		}
		
		//simulate enemy
		Node enemy = new Node();
		enemy.bs = (PentagoBoardState) next.bs.clone();
		Move move2 = enemy.bs.getRandomMove();
		enemy.bs.processMove((PentagoMove)move2);

		if(enemy.bs.getWinner() == maxPlayer)
		{
			result.Wins++;
			return result;
		} 
		else if (enemy.bs.getWinner() == minPlayer)
		{
			result.Losses++;
			return result;
		}
		else if (enemy.bs.getWinner() == Board.DRAW) {
			result.Draws++;
			return result;
		}
		ArrayList<PentagoMove> moves = enemy.bs.getAllLegalMoves();
    	
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
}
