package net.chesstango.uci.arena.listeners;

import net.chesstango.board.Game;
import net.chesstango.board.moves.Move;
import net.chesstango.uci.arena.MatchResult;
import net.chesstango.uci.gui.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mauricio Coria
 */
public class MatchBroadcaster implements MatchListener {

    private List<MatchListener> matchListenerList = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void notifyNewGame(Game game, Controller white, Controller black) {
        matchListenerList.forEach(listener -> listener.notifyNewGame(game, white, black));
    }

    @Override
    public void notifyMove(Game game, Move move) {
        matchListenerList.forEach(listener -> listener.notifyMove(game, move));
    }

    @Override
    public void notifyEndGame(Game game, MatchResult matchResult) {
        matchListenerList.forEach(listener -> listener.notifyEndGame(game, matchResult));
    }

    public MatchBroadcaster addListener(MatchListener listener){
        matchListenerList.add(listener);
        return this;
    }
}
