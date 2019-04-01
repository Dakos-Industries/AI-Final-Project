package student_player;

import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.BoldAction;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class Node {

	public Node Parent;
	
	public ArrayList<Node> children = new ArrayList<Node>();
	
	public double Score = 0;
	
	public Boolean MaxPlayer;
	
	public PentagoBoardState CurrentState;
	
	public PentagoMove Move;
}
