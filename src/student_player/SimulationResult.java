package student_player;

import org.omg.CORBA.PRIVATE_MEMBER;

import sun.management.counter.Variability;

public class SimulationResult {

	public double Wins = 0;
	
	public double Losses = 0;
	
	public double Draws = 0;
	
	public boolean ContinueSim = true;

	public double GetWinLossRatio()
	{
		return (this.Wins + this.Draws * 0.05)/(this.Wins + this.Losses + this.Draws * 0.05);
	}
	
	public int GetTotalTrials()
	{
		return (int)(this.Wins + this.Losses + this.Draws);
	}
	
	public double GetHeuristicScore()
	{
		return (this.Wins - this.Losses + this.Draws * 0.05);
	}
}