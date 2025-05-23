package net.chesstango.tools.perft;

import net.chesstango.board.Game;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.containers.MoveContainerReader;
import net.chesstango.gardel.fen.FEN;

import java.util.*;
import java.util.function.Function;

/**
 * @author Mauricio Coria
 */
public class PerftWithMapIterateDeepening<T> implements Perft {
    private final Function<Game, T> fnGetGameId;
    protected int maxLevel;
    protected int depth;

    protected Map<T, Long[]> transpositionTable;

    public PerftWithMapIterateDeepening(Function<Game, T> fnGetGameId) {
        this.fnGetGameId = fnGetGameId;
        this.transpositionTable = new HashMap<>();
    }

    public PerftResult start(Game game, int depth) {
        this.depth = depth;

        PerftResult result = null;
        for (int i = 1; i <= depth; i++) {
            try {
                result = visitLevel1(game, i);
            } catch (RuntimeException e) {
                System.err.printf("Error with game board: %s\n", game);
                throw e;
            }
        }
        return result;
    }

    protected PerftResult visitLevel1(Game game, int maxLevel) {
        PerftResult perftResult = new PerftResult();
        this.maxLevel = maxLevel;
        long totalNodes = 0;

        T id = fnGetGameId.apply(game);
        Long[] nodeCounts = transpositionTable.computeIfAbsent(id, k -> new Long[depth]);

        for (Move move : game.getPossibleMoves()) {
            long nodeCount = 0;

            if (maxLevel > 1) {
                move.executeMove();

                T childId = fnGetGameId.apply(game);
                Long[] childNodeCounts = transpositionTable.computeIfAbsent(childId, k -> new Long[depth]);

                nodeCount = visitChild(game, 2, childNodeCounts);

                move.undoMove();
            } else {
                nodeCount = 1;
            }

            perftResult.add(move, nodeCount);

            totalNodes += nodeCount;

        }
        perftResult.setTotalNodes(totalNodes);

        nodeCounts[maxLevel - 1] = totalNodes;

        return perftResult;
    }

    protected long visitChild(Game game, int level, Long[] nodeCounts) {
        long totalNodes = 0;

        MoveContainerReader<? extends Move> movimientosPosible = game.getPossibleMoves();

        if (level < this.maxLevel) {
            for (Move move : movimientosPosible) {
                move.executeMove();

                T idChild = fnGetGameId.apply(game);

                Long[] childNodeCounts = transpositionTable.computeIfAbsent(idChild, k -> new Long[depth]);

                if (childNodeCounts[maxLevel - level - 1] == null) {
                    visitChild(game, level + 1, childNodeCounts);
                }

                totalNodes += childNodeCounts[maxLevel - level - 1];

                move.undoMove();
            }
        } else {
            totalNodes = movimientosPosible.size();
        }

        nodeCounts[maxLevel - level] = totalNodes;

        return totalNodes;
    }


    public void printResult(PerftResult result) {
        System.out.println("Total Moves: " + result.getMovesCount());
        System.out.println("Total Nodes: " + result.getTotalNodes());

        Map<Move, Long> childs = result.getChilds();

        if (childs != null) {
            List<Move> moves = new ArrayList<Move>(childs.keySet());
            Collections.reverse(moves);

            for (Move move : moves) {
                System.out.println("Move = " + move.toString() +
                        ", Total = " + childs.get(move));
            }
        }

		/*
		for (int i = 0; i < repeatedNodes.length; i++) {
			System.out.println("Level " + i + " nodes=" + nodeListMap.get(i).size() + " repeated=" + repeatedNodes[i]);
		}*/

        //System.out.println("DefaultLegalMoveGenerator "  + DefaultLegalMoveGenerator.count);
        //System.out.println("NoCheckLegalMoveGenerator "  + NoCheckLegalMoveGenerator.count);
    }


    //TODO: este metodo se esta morfando una parte significativa de la ejecucion
    public static String getStringGameId(Game game) {
        FEN fen = game.getCurrentFEN();
        return String.format("%s %s %s %s", fen.getPiecePlacement(), fen.getActiveColor(), fen.getCastingsAllowed(), fen.getEnPassantSquare());
    }

    public static Long getZobristGameId(Game game) {
        return game.getPosition().getZobristHash();
    }
}
