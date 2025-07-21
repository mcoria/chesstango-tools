package net.chesstango.tools.perft;

import net.chesstango.board.Game;
import net.chesstango.board.Square;
import net.chesstango.board.builders.GameBuilder;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.MovePromotion;
import net.chesstango.board.moves.containers.MoveContainerReader;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.gardel.fen.FENExporter;

import java.util.Map;

/**
 * @author Mauricio Coria
 */
public abstract class AbstractPerftTest {

    protected Game getGame(String string) {
        //GameBuilder builder = new GameBuilderDebug();
        GameBuilder builder = new GameBuilder();

        FENExporter exporter = new FENExporter(builder);

        exporter.export(FEN.of(string));

        return builder.getPositionRepresentation();
    }

    protected Perft createPerft() {
        return new PerftBrute();
        //return new PerftWithMapIterateDeeping<Long>(PerftWithMapIterateDeeping::getZobristGameId);
        //return new PerftWithMap<Long>(PerftWithMap::getZobristGameId);
    }

    protected boolean contieneMove(MoveContainerReader<? extends Move> movimientos, Square from, Square to) {
        for (Move move : movimientos) {
            if (from.equals(move.getFrom().getSquare()) && to.equals(move.getTo().getSquare())) {
                return true;
            }
        }
        return false;
    }

    protected void printForUnitTest(PerftResult result) {

        Map<Move, Long> children = result.getChildren();

        children.entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getKey().toString().compareTo(e2.getKey().toString()))
                .forEach((entry) -> {
                    Move move = entry.getKey();
                    long count = entry.getValue();
                    if (move instanceof MovePromotion movePromotion) {
                        System.out.printf("assertEquals(%d, result.getChildNode(Square.%s, Square.%s, Piece.%s));\n", count, movePromotion.getFrom().getSquare(), movePromotion.getTo().getSquare(), movePromotion.getPromotion().toString());
                    } else {
                        System.out.printf("assertEquals(%d, result.getChildNode(Square.%s, Square.%s));\n", count, move.getFrom().getSquare(), move.getTo().getSquare());
                    }
                });

        System.out.printf("assertEquals(%d, result.getMovesCount());\n", children.size());
        System.out.printf("assertEquals(%d, result.getTotalNodes());\n", result.getTotalNodes());
    }
}
