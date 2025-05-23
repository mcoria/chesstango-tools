package net.chesstango.tools.perft;

import net.chesstango.board.Game;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.containers.MoveContainerReader;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Mauricio Coria
 */
public class PerftBrute implements Perft {

    private int maxLevel;


    @Override
    public PerftResult start(Game game, int maxLevel) {
        this.maxLevel = maxLevel;
        PerftResult perftResult = new PerftResult();
        long totalNodes = 0;

        long zobristHashBefore = game.getPosition().getZobristHash();
        for (Move move : game.getPossibleMoves()) {
            long nodeCount = 0;

            if (maxLevel > 1) {
                move.executeMove();

                nodeCount = visitChild(game, 2);

                move.undoMove();

                long zobristHashAfter = game.getPosition().getZobristHash();
                if (zobristHashBefore != zobristHashAfter) {
                    throw new RuntimeException("hashBefore != hashAfter");
                }
            } else {
                nodeCount = 1;
            }

            perftResult.add(move, nodeCount);

            totalNodes += nodeCount;

        }

        perftResult.setTotalNodes(totalNodes);

        return perftResult;
    }

    private long visitChild(Game game, int level) {
        long totalNodes = 0;

        MoveContainerReader<? extends Move> movimientosPosible = game.getPossibleMoves();

        if (level < this.maxLevel) {
            long zobristHashBefore = game.getPosition().getZobristHash();

            for (Move move : movimientosPosible) {
                move.executeMove();

                totalNodes += visitChild(game, level + 1);

                move.undoMove();
                long zobristHashAfter = game.getPosition().getZobristHash();
                if (zobristHashBefore != zobristHashAfter) {
                    throw new RuntimeException("hashBefore != hashAfter");
                }
            }
        } else {
            totalNodes = movimientosPosible.size();
        }

        return totalNodes;
    }


    public void printResult(PerftResult perftResult) {
        System.out.println("Total Moves: " + perftResult.getMovesCount());
        System.out.println("Total Nodes: " + perftResult.getTotalNodes());

        Map<Move, Long> childs = perftResult.getChilds();

        if (childs != null) {
            List<Move> moves = new ArrayList<Move>(childs.keySet());
            Collections.reverse(moves);

            for (Move move : moves) {
                System.out.println("Move = " + move.toString() +
                        ", Total = " + childs.get(move));
            }
        }
        //System.out.println("DefaultLegalMoveGenerator "  + DefaultLegalMoveGenerator.count);
        //System.out.println("NoCheckLegalMoveGenerator "  + NoCheckLegalMoveGenerator.count);
        //System.out.println("MAX_MOVECOUNTER_SIZE = "  + NoCheckLegalMoveGenerator.MAX_MOVECOUNTER_SIZE);
    }

}
