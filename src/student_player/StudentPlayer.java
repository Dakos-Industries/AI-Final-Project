package student_player;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260689391");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {        
        ZeroSystem zeroSystem = new ZeroSystem(boardState);
        Move attempt = zeroSystem.EngageZeroSystem();
        
        // Return your move to be processed by the server.
        return attempt;
    }
}