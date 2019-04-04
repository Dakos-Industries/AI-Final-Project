package student_player;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Parent;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class Node {

	public Node Parent;
	
	public PentagoBoardState bs;
	
	/**
	 * To be used for some pruning
	 * */
	public SimulationResult result = new SimulationResult();
	
	public Node(PentagoBoardState bs)
	{
		this.bs = (PentagoBoardState) bs.clone();
	}
	
	Map<PentagoMove, Node> Children = new HashMap<PentagoMove, Node>();
	
	public void IncrementWin()
	{
		Node parent = this.Parent;
		this.result.Wins++;
		while(parent != null)
		{
			parent.result.Wins++;
			parent = parent.Parent;
		}
	}
	
	public void IncrementLosses()
	{
		Node parent = this.Parent;
		this.result.Losses++;
		while(parent != null)
		{
			parent.result.Losses++;
			parent = parent.Parent;
		}
	}
	
	public void IncrementLosses(int amount)
	{
		Node parent = this.Parent;
		this.result.Losses += amount;
		while(parent != null)
		{
			parent.result.Losses += amount;
			parent = parent.Parent;
		}
	}
	
	public void IncrementDraws()
	{
		Node parent = this.Parent;
		this.result.Draws++;
		while(parent != null)
		{
			parent.result.Draws++;
			parent = parent.Parent;
		}
	}
}
