package student_player;

import pentago_swap.PentagoMove;

public class CandidateMove implements Comparable<CandidateMove>{

	public PentagoMove Move;
	
	public double WinLossRatio = 0;
	
	public double HeuristicScore = 0;
	
	public CandidateMove(PentagoMove move, double winLossRatio, double heuristicScore) {
		// TODO Auto-generated constructor stub
		this.Move = move;
		this.WinLossRatio = winLossRatio;
		this.HeuristicScore = heuristicScore;
	}

	@Override
	public int compareTo(CandidateMove a) {
		double scoreA = a.HeuristicScore;

	    if (this.HeuristicScore == scoreA) {
	        return 0;
	    }
	    else if (this.HeuristicScore > scoreA) {
	        return 1;
	    }
	    else {
	        return -1;
	    }
	}
}
