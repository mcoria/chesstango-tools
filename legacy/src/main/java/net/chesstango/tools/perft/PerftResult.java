package net.chesstango.tools.perft;

import lombok.Getter;
import lombok.Setter;
import net.chesstango.board.Piece;
import net.chesstango.board.Square;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.MovePromotion;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Mauricio Coria
 */
public class PerftResult {

    @Setter
    @Getter
    private long totalNodes;

    private final Map<Move, Long> moves = new HashMap<Move, Long>();

    public void add(Move move, long nodeCount) {
        moves.put(move, nodeCount);
    }

    public long getMovesCount() {
        return moves.size();
    }

    public Map<Move, Long> getChildren() {
        return moves;
    }


    public long getChildNode(Square from, Square to) {
        for (Entry<Move, Long> entry : moves.entrySet()) {
            Move move = entry.getKey();
            if (move.getFrom().getSquare().equals(from) && move.getTo().getSquare().equals(to)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("Move not found");
    }

    public long getChildNode(Square from, Square to, Piece piece) {
        for (Entry<Move, Long> entry : moves.entrySet()) {
            Move move = entry.getKey();
            if (move.getFrom().getSquare().equals(from) && move.getTo().getSquare().equals(to) && move instanceof MovePromotion movePromotion && movePromotion.getPromotion().equals(piece)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("Move not found");
    }

    public boolean moveExists(Square from, Square to) {
        for (Entry<Move, Long> entry : moves.entrySet()) {
            Move move = entry.getKey();
            if (move.getFrom().getSquare().equals(from) && move.getTo().getSquare().equals(to)) {
                return true;
            }
        }
        return false;
    }
}
