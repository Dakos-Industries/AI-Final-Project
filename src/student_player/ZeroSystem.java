package student_player;

import java.util.ArrayList;
import java.util.Collections;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/**
 * The AI Agent Code Name = Zero System
 * 
 * @author Spiro ID: 260689391
 * @version 2.0
 */
public class ZeroSystem {
	private int TrialLimit = 300;

	/**
	 * The current board state
	 */
	private PentagoBoardState CurrentState;

	/**
	 * Constructor for the Zero System AI agent
	 * @param pbs The BoardState that we wish to run the simulation from.
	 */
	public ZeroSystem(PentagoBoardState pbs) {
		this.CurrentState = this.CloneBoard(pbs);
	}

	/**
	 * This method will find the "best" move by running a Monte-Carlo style simulation.
	 * 
	 * @return Move The best move from the simulation.
	 */
	public Move EngageZeroSystem() {
		ArrayList<PentagoMove> moves = this.CurrentState.getAllLegalMoves();
		PentagoMove candidateMove = moves.get(0);
		double bestRatio = 0;

		for (PentagoMove move : moves) {
			PentagoBoardState tmpBoard = this.CloneBoard(this.CurrentState);
			tmpBoard.processMove(move);

			// Check to see if the move will make us the winner, End the simulation then as
			// it is the best move.
			if (tmpBoard.getWinner() == this.CurrentState.getTurnPlayer()) {
				candidateMove = move;
				break;
			}

			// Check if our move will lead to an enemy victory
			// Skip the move if it does
			if (this.CanEnemyWin(tmpBoard)) {
				continue;
			}

			// We have no winner so run the simulation to gather statistics
			SimulationResult result = PerformMonteCarloSimulation(this.CurrentState, move, new SimulationResult());

			// Take the move with the best win/loss ratio
			if (result.GetWinLossRatio() > bestRatio) {
				bestRatio = result.GetWinLossRatio();
				candidateMove = move;
			}
		}
		return candidateMove;
	}

	/**
	 * Runs simulations until the number of simulations is the same as the TrialLimit while keeping 
	 * track of wins, losses and draws.
	 * 
	 *  @param current The current board state.
	 *  @param move The move to be applied.
	 *  
	 *  @return SimulationResult The results of the simulation.
	 * */
	private SimulationResult PerformMonteCarloSimulation(PentagoBoardState current, PentagoMove move,
			SimulationResult result) {
		if (result.GetTotalTrials() >= this.TrialLimit) {
			result.ContinueSim = false;
			return result;
		}

		PentagoBoardState next = this.CloneBoard(current);
		int maxPlayer = next.getTurnPlayer();
		next.processMove(move);

		int minPlayer = next.getTurnPlayer();

		// Check to see if our move resulted in someone winning or a draw
		if (UpdateResults(maxPlayer, minPlayer, next, result)) {
			return result;
		}

		// simulate enemy
		PentagoBoardState enemy = this.CloneBoard(next);

		// Check if our move lead to a move where the enemy could win
		// Return a loss if it did as enemy would have chosen it
		if (this.CanEnemyWin(enemy)) {
			// This is an expensive operation so make it take
			// up a good amount of trials or we will time out
			result.Losses += 15;
			return result;
		}

		// Enemy has no move to win instantly so chose random move and continue
		Move move2 = enemy.getRandomMove();
		enemy.processMove((PentagoMove) move2);

		// Check to see if the enemy resulted in someone winning or a draw
		if (this.UpdateResults(maxPlayer, minPlayer, enemy, result)) {
			return result;
		}

		// This corresponds to our moves
		ArrayList<PentagoMove> moves = enemy.getAllLegalMoves();

		// add some randomness (Done as we might not get to run through every move. I.e. when the trial limit is reached)
		Collections.shuffle(moves);

		for (PentagoMove pentagoMove : moves) {
			PerformMonteCarloSimulation(enemy, pentagoMove, result);
			if (!result.ContinueSim) {
				return result;
			}
		}
		return result;
	}

	/**
	 * Check if the enemy can win with any of their next moves
	 * 
	 */
	private boolean CanEnemyWin(PentagoBoardState enemyTurn) {
		ArrayList<PentagoMove> enemyMoves = enemyTurn.getAllLegalMoves();
		if (enemyMoves.size() > 0) {
			for (PentagoMove enemyMove : enemyMoves) {
				PentagoBoardState simBoard = this.CloneBoard(enemyTurn);
				simBoard.processMove(enemyMove);

				// enemy is the opponent
				if (simBoard.getWinner() == simBoard.getOpponent()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return true is results were updated; false otherwise
	 * 
	 */
	private Boolean UpdateResults(int maxPlayer, int minPlayer, PentagoBoardState pbs, SimulationResult result) {
		int winner = pbs.getWinner();
		if (winner == maxPlayer) {
			result.Wins++;
			return true;
		} else if (winner == minPlayer) {
			result.Losses++;
			return true;
		} else if (winner == Board.DRAW) {
			result.Draws++;
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to clone boards.
	 * */
	private PentagoBoardState CloneBoard(PentagoBoardState boardState) {
		return (PentagoBoardState) boardState.clone();
	}
}