package pentago_swap;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
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
        return boardState.getRandomMove();
    }
}
