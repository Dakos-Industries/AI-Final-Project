package student_player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.activation.UnsupportedDataTypeException;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.org.apache.xpath.internal.axes.ChildIterator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import javafx.scene.Parent;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import sun.net.www.content.audio.x_aiff;
import sun.nio.cs.ext.DoubleByteEncoder;

public class MyTools {
	
	private static int FrontierSize = 2;
	
	private static int Iteration = 0;
	
    public static double getSomething() {
        return Math.random();
    }
    
    public static Move EngageZeroSystem(PentagoBoardState bs)
    {
    	// Creating the tree
    	Node root = new Node();
        root.CurrentState = CloneBoard(bs);
        
        // Max player turn
        root.MaxPlayer = true;
        
        // Expand the frontier
        int frontierSize = FrontierSize;
        ArrayList<PentagoMove> moves = root.CurrentState.getAllLegalMoves();
        
        int maxDepth = 0;
        
        if(bs.getTurnNumber() > 12)
        {
        	frontierSize = 14;
        	maxDepth = 18;
        }
        else if (bs.getTurnNumber() >= 5){
        	frontierSize = 10;
        	maxDepth = 12;
		}
        else if(bs.getTurnNumber() > 3) {
        	frontierSize = 10;
        	maxDepth = 10;
		}
        else {
        	frontierSize = 14;
			maxDepth = 5;
		}
        
        if (moves.size() < FrontierSize)
        {
        	frontierSize = moves.size();
        }
        
        Collections.shuffle(moves);
        
        // Populate the frontier
        for(int i = 0; i < frontierSize; i++)
        {
        	Node tmp = new Node();
        	tmp.CurrentState = CloneBoard(root.CurrentState);
        	tmp.CurrentState.processMove(moves.get(i));
        	
        	// Now its the min player turn
        	tmp.MaxPlayer = false;
        	tmp.Parent = root;
        	tmp.Move = moves.get(i);
        	root.children.add(tmp);
        	Iteration++;
        }
        
        // Now we must simulate the game
        BeginSimulation(root, maxDepth);
        
        // Now we evaluate the moves and chose the best option
        AnalyzeStatistics(root);
        
        Move move = ChooseMove(root);
        
        //return move
        return move;
    }
    
    private static Move ChooseMove(Node root)
    {
    	double bestScore = 0;
    	Move bestMove = null;
    	
    	for(int i =0; i < root.children.size(); i++)
    	{
    		Node child = root.children.get(i);
    		if(i == 0) {
    			bestMove = child.Move;
    			bestScore = child.Score;
    		}
    		else if (child.Score > bestScore)
    		{
    			bestMove = child.Move;
    			bestScore = child.Score;
    		}
    	}
        System.out.println("Best score" + bestScore);
    	return bestMove;
    }
    private static void AnalyzeStatistics(Node node)
    {
    	for (Node child : node.children) {
			if (child.children.size() > 0) {
				AnalyzeStatistics(child);
			}
			else
			{
				// If the leaf is a max player then the min player played prior
				if (child.MaxPlayer) {
					
					// we lost
					if(child.Score < 0) {
						
						child.Parent.Score += child.Score;
					}
					else if(child.Score >= 0)
					{
						child.Parent.Score += child.Score;
					}
				}
				else // the max player played
				{
					child.Parent.Score += child.Score;
				}
			}
			child.Parent.Score += child.Score; 
		}
    }
    
    private static void  BeginSimulation(Node current, int maxDepth)
    {
    	//System.out.println("Iteration "+ Iteration);
    	for (int i = 0; i < current.children.size(); i++) {
    		
    		Node child = current.children.get(i);
    		
    		// Check if we have a winner
        	int maxPlayer = 0;
        	int minId = 0;
        	int winner = child.CurrentState.getWinner();
        	
        	if(child.MaxPlayer)
        	{
        		maxPlayer = child.CurrentState.getTurnPlayer();
            	minId = child.CurrentState.getOpponent();
        	}
        	else
        	{
        		minId = child.CurrentState.getTurnPlayer();
            	maxPlayer = child.CurrentState.getOpponent();
        	}
        	
        	// Return if we have a winner
        	if(winner == maxPlayer)
    		{
        		child.Score++;
        		continue;
    		} 
    		else if (winner == minId)
    		{
    			child.Score--;
    			continue;
    		}
    		else if (winner == Board.DRAW) {
    			child.Score += 0.75;
    			continue;
    		}
    		
        	if(maxDepth <= current.CurrentState.getTurnNumber())
        	{
        		continue;
        	}
        	
			if (!child.MaxPlayer)
			{
				// This branch will lead to a loss if enemy plays optimally 
				if(CutBranch(child))
				{
					current.Score--;
					current.children.remove(i);
					continue;
				}
				
				PerformMinMove(child, maxDepth);
			}
			else
			{
				PerformMaxMove(child, maxDepth);
			}
		}    	
    }
      
    private static void PerformMinMove(Node minNode, int maxDepth) {    	
        // Expand the frontier
        int frontierSize = FrontierSize;
        ArrayList<PentagoMove> moves = minNode.CurrentState.getAllLegalMoves();
        
        if (moves.size() < FrontierSize)
        {
        	frontierSize = moves.size();
        }
        
        Collections.shuffle(moves);
        
        // Populate the frontier
        for(int i = 0; i < frontierSize; i++)
        {
        	Node tmp = new Node();
        	tmp.CurrentState = CloneBoard(minNode.CurrentState);
        	tmp.CurrentState.processMove(moves.get(i));
        	
        	// Now its the max player turn
        	tmp.MaxPlayer = true;
        	tmp.Parent = minNode;
        	minNode.children.add(tmp);
        	Iteration++;
        }	
        
        BeginSimulation(minNode, maxDepth);
    }
    
    private static void PerformMaxMove(Node maxNode, int maxDepth) {
        // Expand the frontier
        int frontierSize = FrontierSize;
        ArrayList<PentagoMove> moves = maxNode.CurrentState.getAllLegalMoves();
        
        if (moves.size() < FrontierSize)
        {
        	frontierSize = moves.size();
        }
        
        Collections.shuffle(moves);
        
        // Populate the frontier
        for(int i = 0; i < frontierSize; i++)
        {
        	Node tmp = new Node();
        	tmp.CurrentState = CloneBoard(maxNode.CurrentState);
        	tmp.CurrentState.processMove(moves.get(i));
        	
        	// Now its the min player turn
        	tmp.MaxPlayer = false;
        	tmp.Parent = maxNode;
        	maxNode.children.add(tmp);
        	Iteration++;
        }
    	BeginSimulation(maxNode, maxDepth);
    }
    
    /*
     * Check if opponent wins.
     * If they do return true so we can know to cut the branch
     * */
    private static boolean CutBranch(Node node)
    {
    	PentagoBoardState tmpBoard = CloneBoard(node.CurrentState);
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
	    	    	return true;
	    	    }
    	    }    
	    }	
	    return false;
    }
    
    private static PentagoBoardState CloneBoard(PentagoBoardState bs)
    {
    	return (PentagoBoardState) bs.clone();
    }

}