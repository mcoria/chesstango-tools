package net.chesstango.uci.arena.listeners;

import lombok.Getter;
import net.chesstango.board.Color;
import net.chesstango.board.Game;
import net.chesstango.board.iterators.state.FirstToLast;
import net.chesstango.board.iterators.state.StateIterator;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.generators.pseudo.MoveGenerator;
import net.chesstango.board.position.ChessPositionReader;
import net.chesstango.board.position.GameStateReader;
import net.chesstango.mbeans.Arena;
import net.chesstango.mbeans.GameDescriptionCurrent;
import net.chesstango.mbeans.GameDescriptionInitial;
import net.chesstango.uci.arena.MatchResult;
import net.chesstango.uci.gui.Controller;

import java.util.*;

/**
 * @author Mauricio Coria
 */
public class MatchListenerToMBean implements MatchListener {

    @Getter
    private final Arena arena;

    private volatile String currentGameId;

    public MatchListenerToMBean() {
        this(Arena.createAndRegisterMBean());
    }
    
    public MatchListenerToMBean(Arena arena) {
        this.arena = arena;
    }


    @Override
    public void notifyNewGame(Game game, Controller white, Controller black) {
        currentGameId = UUID.randomUUID().toString();

        String whiteName = white.getEngineName();

        String blackName = black.getEngineName();

        String turn = Color.WHITE.equals(game.getChessPosition().getCurrentTurn()) ? "white" : "black";

        GameDescriptionInitial gameDescriptionInitial = new GameDescriptionInitial(currentGameId, game.getInitialFEN().toString(), whiteName, blackName, turn);

        arena.newGame(gameDescriptionInitial);
    }


    @Override
    public void notifyMove(Game game, Move move) {
        List<String> theMoves = new ArrayList<>();

        StateIterator stateIterator = new FirstToLast(game.getState());
        while (stateIterator.hasNext()) {
            GameStateReader gameState = stateIterator.next();

            Move aMove = gameState.getSelectedMove();

            theMoves.add(encodeMove(aMove));
        }

        String[] arrayMoveStr = theMoves.toArray(String[]::new);

        String turn = Color.WHITE.equals(game.getChessPosition().getCurrentTurn()) ? "white" : "black";

        String lastMove = encodeMove(move);

        GameDescriptionCurrent gameDescriptionCurrent = new GameDescriptionCurrent(currentGameId, game.getCurrentFEN().toString(), turn, lastMove, arrayMoveStr);

        arena.newMove(gameDescriptionCurrent);
    }

    @Override
    public void notifyEndGame(Game game, MatchResult matchResult) {
    }

    // TODO: obviously some moves are not encoded properly
    protected static String encodeMove(Move move) {
        return String.format("%s-%s", move.getFrom().getSquare(), move.getTo().getSquare());
    }

}