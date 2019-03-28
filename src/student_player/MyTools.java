package student_player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.lang.reflect.Array;
import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import sun.net.www.content.audio.x_aiff;

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }
    
    public static Move GetBestMove(Node current)
    {
    	ArrayList<PentagoMove> moves = current.bs.getAllLegalMoves();
    	
    	PentagoMove candidateMove = moves.get(0);
    	double bestRatio = 0;
    	int attempt = 0;
    	for(PentagoMove move : moves)
    	{
    		attempt++;
    		SimulationResult result = CountingDFS(current, move, new SimulationResult());
    		if (result.GetWinLossRatio() > bestRatio)
    		{
    			bestRatio = result.GetWinLossRatio();
    			candidateMove = move;
    			System.out.println("Attemp " + attempt + "| Ratio " + bestRatio + "|Games played " + (result.Losses + result.Wins + result.Draws));
    		}
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

    public static PentagoMove MonteCarlo(Node current)
    {
    	ArrayList<PentagoMove> moves = current.bs.getAllLegalMoves();
    	CandidateMove[] candidates = new CandidateMove[5];
    	
    	int i = 0;
    	for(PentagoMove move : moves)
    	{
    		SimulationResult result = CountingDFS(current, move, new SimulationResult());
    		if(i < 5)
    		{
    			candidates[i] = new CandidateMove(move, result.GetWinLossRatio(), result.GetHeuristicScore());
    			if (i == 4) {
    				Arrays.sort(candidates);
				}
    			i++;
    			continue;
    		}
    		
    		if (result.GetHeuristicScore() > candidates[0].HeuristicScore)
    		{
    			candidates[0] = new CandidateMove(move, result.GetWinLossRatio(), result.GetHeuristicScore());
    		}
    	}
    	
    	PentagoMove bestMove = candidates[0].Move;
    	for (int k = 0; k < candidates.length; k++)
    	{
    		Node nextLevel = new Node();
    		nextLevel.bs = (PentagoBoardState) current.bs.clone();
    		nextLevel.bs.processMove(candidates[k].Move);
    		
    	}
    	
    	return bestMove;
    }



    private static SimulationResult InitializeMC(Node current, PentagoMove moveA, PentagoMove moveB)
    {
    	SimulationResult result = new SimulationResult();
		Node next = new Node();
		next.bs = (PentagoBoardState) current.bs.clone();
		int maxPlayer = next.bs.getTurnPlayer();
		next.bs.processMove(moveA);
		
		int minPlayer = next.bs.getTurnPlayer();
		if(next.bs.getWinner() == maxPlayer)
		{
			result.Wins = 30;
			return result;
		} 
		else if (next.bs.getWinner() == minPlayer)
		{
			result.Losses = 30;
			return result;
		}
		else if (next.bs.getWinner() == Board.DRAW) {
			result.Draws = 30;
			return result;
		}
		
		//simulate enemy
		Node enemy = new Node();
		enemy.bs = (PentagoBoardState) next.bs.clone();
		
		// Now we want to run all the enemy moves
		ArrayList<PentagoMove> moves = enemy.bs.getAllLegalMoves();
    	for(PentagoMove pentagoMove : moves)
    	{
    		Move move2 = enemy.bs.getRandomMove();
    		enemy.bs.processMove((PentagoMove)move2);

    		if(enemy.bs.getWinner() == maxPlayer)
    		{
    			result.Wins++;
    		} 
    		else if (enemy.bs.getWinner() == minPlayer)
    		{
    			result.Losses++;
    		}
    		else if (enemy.bs.getWinner() == Board.DRAW) {
    			result.Draws++;
    		}
    		
    		SimulationResult simulationResult = CountingDFS(enemy, pentagoMove, result);
    		if(!simulationResult.ContinueSim)
    		{
    			return result;
    		}
    	}
    	
		return result;
    }

}