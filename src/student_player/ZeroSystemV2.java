package student_player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map; 
import java.util.HashMap;
import java.util.Set;

import org.omg.CORBA.PRIVATE_MEMBER;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class ZeroSystemV2 {
	
	private int TrialLimit = 150;
	
	private Node CurrentNode;
	
	private int CurrentTrial = 0;
	
	private int CurrentTurn = 0;
	
	private double PriorBestRatio = 0;
	
	/**
	 * The current board state
	 * */
	private PentagoBoardState CurrentState;
	
	/**
	 * Constructor for the Zero System AI agent
	 * */
	public ZeroSystemV2(){
		
	}
	
	/**
	 * Use the algorithm to find the Best Move
	 * */
	public Move EngageZeroSystem(PentagoBoardState pbs)
	{
		this.CurrentState = this.CloneBoard(pbs);
		this.SetCurrentNode();
		this.CurrentTurn = pbs.getTurnNumber();
		
		ArrayList<PentagoMove> moves = this.CurrentState.getAllLegalMoves();	
    	PentagoMove candidateMove = moves.get(0);
    	double bestRatio = 0;
    	Collections.shuffle(moves);
    	
    	System.out.println("Turn " + this.CurrentTurn);
    	if(this.CurrentTurn == 0)
    	{
    		this.TrialLimit = 1000;
    	}
    	else if (this.CurrentTurn <= 4) {
			this.TrialLimit = 50;
		}
    	else
    	{
    		this.TrialLimit = 175;
    	}
    	
    	for(int i =0; i < moves.size(); i++)
    	{    
    		Node child = null;
    		
    	    child = this.MoveAlreadyApplied(this.CurrentNode, moves.get(i));
    		
    	    if(child != null)
    	    {
    	    	// We chose to go into this state so it should be max 
    	    	if(child.result.GetWinLossRatio() <= this.PriorBestRatio)
    	    	{
    	    		continue;
    	    	}
    	    }
    	    
    		PentagoBoardState tmpBoard = this.CloneBoard(this.CurrentState);
    	    tmpBoard.processMove(moves.get(i));
    	    
    	    // Check to see if the move will make us the winner, End the simulation then as it is the best move.
    	    if(tmpBoard.getWinner() == this.CurrentState.getTurnPlayer())
    	    {
    	    	candidateMove = moves.get(i);
    	    	System.out.println("I win " + this.CurrentState.getTurnNumber());
    	    	break;
    	    }
    	    
    	    if(this.CurrentTurn >= 5) {
    	    	// Check if our move will lead to an enemy victory
    	    	// Skip the move if it does
	    	    if (this.CanEnemyWin(tmpBoard))
	    	    {
	    	    	continue;
	    	    }
    	    }
    		
    		if(child != null)
    		{
    			// prune if it has been seen before
    			this.ApplyDFSonAppliedMove(child);
    		}
    		else
    		{
    			// We have no winner so run the simulation to gather statistics
        		DepthLimitedSimulation(this.CurrentNode, moves.get(i));
    		}
    		
    		if(child == null)
    		{
    			child = this.MoveAlreadyApplied(this.CurrentNode, moves.get(i));
    		}
    		
    		//System.out.println("Found match:" + child.result.GetTotalTrials());
    		if (child.result.GetWinLossRatio() > bestRatio)
    		{
    			bestRatio = child.result.GetWinLossRatio();
    			candidateMove = moves.get(i);
    		}
    		
    		this.CurrentTrial = 0;
    		
    	}
    	
    	this.PriorBestRatio = bestRatio; 
    			
    	// set the current node
    	this.CurrentNode = this.MoveAlreadyApplied(this.CurrentNode, candidateMove);
    	
    	if(this.CurrentNode != null)
    	{
    		this.CurrentNode.Parent = null;
    	}   
    	
    	return candidateMove;
	}
	
	private Node MoveAlreadyApplied(Node node, PentagoMove move)
	{
		if (node.Children.isEmpty())
		{
			return null;
		}
		
		return node.Children.getOrDefault(move, null);
	}
	
	private void SetCurrentNode() {
		if (this.CurrentNode == null)
		{
			this.CurrentNode = new Node(this.CurrentState);
		}
		else
		{
			// Check If the move was applied
			for (Node  node : this.CurrentNode.Children.values()) {
				if(node.bs.equals(this.CurrentState))
				{
					System.out.println("Child found");
					this.CurrentNode = node;
					return;
				}
			}
			
			this.CurrentNode = new Node(this.CurrentState);
		}
	}
	
	private void ApplyDFSonAppliedMove(Node child)
	{
		for (Node  node : child.Children.values()) {
			
			// Opponent wants to minimize
			if (node.result.GetWinLossRatio() < this.PriorBestRatio) {
				
				for (PentagoMove move : node.bs.getAllLegalMoves()) {
					
					Node prune2 = this.MoveAlreadyApplied(node, move);
					
					if(prune2 != null) {
						// We want to maximize
						if(prune2.result.GetWinLossRatio() >= this.PriorBestRatio)
						{
							DepthLimitedSimulation(child, move);
						}
					}
				}
			}
		}
	}
	
	private Node DepthLimitedSimulation(Node current, PentagoMove move)
    {	
		this.CurrentTrial++;
		
		if(this.CurrentTrial >= this.TrialLimit)
		{
			return current;
		}
		int maxPlayer = current.bs.getTurnPlayer();
		int minPlayer = current.bs.getOpponent();
		Node child = null;
		
		if (true)
		{
			child = this.MoveAlreadyApplied(current, move);
		}
		
		if (child == null)
		{
			PentagoBoardState next = this.CloneBoard(current.bs);
			next.processMove(move);
			
			child = new Node(next);
			child.Parent = current;
			current.Children.put(move, child);			
		}
		
		// Check to see if our move resulted in someone winning or a draw
		if(UpdateResults(maxPlayer, minPlayer, child))
		{
			return current;
		}
		
		//simulate enemy
		PentagoBoardState enemy = this.CloneBoard(child.bs);
		
		// Check if our move lead to a move where the enemy could win
		// Return a loss if it did as enemy would have chosen it
		if(this.CanEnemyWin(enemy))
		{
			// This is an expensive operation so make it take
			// up a good amount of trials or we will time out
			child.IncrementLosses();
			//child.SetNegativeScore();
			return current;
		}
		
		
		// Enemy has no move to win instantly so chose random move and continue
		
		PentagoMove move2 = this.GetWorstMove(child);
		
		// explore unexplored moves
		enemy.processMove(move2);

		
		Node enemyNode = this.MoveAlreadyApplied(child, move2);
		
		if (enemyNode == null) {
			enemyNode = new Node(enemy);
			enemyNode.Parent = child;
			child.Children.put(move2, enemyNode);
		}
		
		// Check to see if the enemy resulted in someone winning or a draw
		if(this.UpdateResults(maxPlayer, minPlayer, enemyNode))
		{
			return current;
		}
		
		//	This corresponds to our moves
		ArrayList<PentagoMove> moves = enemy.getAllLegalMoves();
    	
		// add some randomness
		Collections.shuffle(moves);
		
    	for(PentagoMove pentagoMove : moves)
    	{	
    		DepthLimitedSimulation(enemyNode, pentagoMove);
    		
    		// We got the data we need so lets return!
    		if(this.CurrentTrial >= this.TrialLimit)
    		{   
    			return current;
    		}
    	}
    	
		return current;
    }
	
	private PentagoMove GetWorstMove(Node current)
	{
		if(current.Children.isEmpty())
		{
			return (PentagoMove) current.bs.getRandomMove();
		}
		

		for(Map.Entry<PentagoMove, Node> entry : current.Children.entrySet())
		{
			if(entry.getValue().result.GetWinLossRatio() < this.PriorBestRatio)
			{
				System.out.println("Worst");
				return entry.getKey();
			}
		}
		
		return (PentagoMove) current.bs.getRandomMove();
	}
    
	/**
	 * Check if the enemy can win with any of their next moves
	 * 
	 * */
	private boolean CanEnemyWin(PentagoBoardState enemyTurn) {
		ArrayList<PentagoMove> enemyMoves = enemyTurn.getAllLegalMoves();	
	    if (enemyMoves.size() > 0)
	    {
    	    for(PentagoMove enemyMove : enemyMoves)
    	    {
    	    	PentagoBoardState simBoard = this.CloneBoard(enemyTurn);
    	    	simBoard.processMove(enemyMove);
    	    	
	    	    // enemy is the opponent
	    	    if (simBoard.getWinner() == simBoard.getOpponent())
	    	    {	    	    	
	    	    	return true;
	    	    }
    	    }    
	    }
	    return false;
	}  
	
    /**
     * Return true is results were updated; false otherwise
     * 
     * */
    private Boolean UpdateResults(int maxPlayer, int minPlayer, Node node)
    {
    	int winner = node.bs.getWinner();
    	if(winner == maxPlayer)
		{
    		node.IncrementWin();
			return true;
		} 
		else if (winner == minPlayer)
		{
			node.IncrementLosses();;
			return true;
		}
		else if (winner == Board.DRAW) {
			node.IncrementDraws();
			return true;
		}
    	return false;
    }
	

	private PentagoBoardState CloneBoard(PentagoBoardState boardState) {
		return (PentagoBoardState) boardState.clone();
	}

}
