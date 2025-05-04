package net.chesstango.tools.perft;

import net.chesstango.board.Game;
import net.chesstango.board.Square;
import net.chesstango.board.builders.GameBuilder;
import net.chesstango.board.builders.GameBuilderDebug;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.containers.MoveContainerReader;
import net.chesstango.board.representations.fen.FEN;
import net.chesstango.board.representations.fen.FENExporter;
import net.chesstango.tools.perft.imp.PerftBrute;

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

        Map<Move, Long> childs = result.getChilds();

        childs.forEach((move, count) -> {
            System.out.printf("assertEquals(%d, result.getChildNode(Square.%s, Square.%s));\n", count, move.getFrom().getSquare(), move.getTo().getSquare());
        });

        System.out.printf("assertEquals(%d, result.getMovesCount());\n", childs.size());
        System.out.printf("assertEquals(%d, result.getTotalNodes());\n", result.getTotalNodes());
    }
}
