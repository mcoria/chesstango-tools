package net.chesstango.tools.perft;

import net.chesstango.board.Game;
import net.chesstango.board.Piece;
import net.chesstango.board.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * @author Mauricio Coria
 */
public class PerftDifferentPositionsTest extends AbstractPerftTest {

    private Perft perft;

    private Game game;

    @BeforeEach
    public void setUp() throws Exception {
        perft = createPerft();
    }


    @Test
    public void test01() {
        game = getGame("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1");

        PerftResult result = perft.start(game, 5);

        assertEquals(532933, result.getTotalNodes());
    }


    @Test
    public void test02() {
        game = getGame("r3r1k1/pp1n1ppp/2p5/4Pb2/2B2P2/B1P5/P5PP/R2R2K1 w - - 0 1");

        PerftResult result = perft.start(game, 5);

        assertEquals(46934059, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test03() {
        game = getGame("r3r1k1/pp1n1ppp/2p5/4Pb2/2B2P2/B1P5/P5PP/R2R2K1 w - - 0 1");

        PerftResult result = perft.start(game, 6);

        assertEquals(1478670842, result.getTotalNodes());
    }

    @Test
    public void test04() {
        game = getGame("r3k2r/p1pp1pb1/bn1qpnpB/3PN3/1p2P3/2N2Q1p/PPP1BPPP/R2K3R b kq - 3 2");

        game.executeMove(Square.c7, Square.c5);

        PerftResult result = perft.start(game, 1);

        assertFalse(result.moveExists(Square.d5, Square.c6));    // En Passant capture deja el rey al descubierto

        assertEquals(44, result.getMovesCount());
        assertEquals(44, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test05() {
        game = getGame("8/8/4k3/8/4K3/8/8/8 w KQkq - 0 1");

        PerftResult result = perft.start(game, 6);
        //printForUnitTest(result);

        assertEquals(15566, result.getChildNode(Square.e4, Square.f4));
        assertEquals(23000, result.getChildNode(Square.e4, Square.e3));
        assertEquals(23876, result.getChildNode(Square.e4, Square.f3));
        assertEquals(15566, result.getChildNode(Square.e4, Square.d4));
        assertEquals(23876, result.getChildNode(Square.e4, Square.d3));
        assertEquals(5, result.getMovesCount());
        assertEquals(101884, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test05_mirror() {
        game = getGame("8/8/4k3/8/4K3/8/8/8 w KQkq - 0 1").mirror();

        PerftResult result = perft.start(game, 6);
        //printForUnitTest(result);

        assertEquals(15566, result.getChildNode(Square.e5, Square.d5));
        assertEquals(23876, result.getChildNode(Square.e5, Square.d6));
        assertEquals(23000, result.getChildNode(Square.e5, Square.e6));
        assertEquals(23876, result.getChildNode(Square.e5, Square.f6));
        assertEquals(15566, result.getChildNode(Square.e5, Square.f5));
        assertEquals(5, result.getMovesCount());
        assertEquals(101884, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test06() {
        game = getGame("8/6n1/4k3/8/4K3/2N5/8/8 w - - 0 1");

        PerftResult result = perft.start(game, 7);
        //printForUnitTest(result);

        assertEquals(1095564, result.getChildNode(Square.c3, Square.d1));
        assertEquals(2762987, result.getChildNode(Square.e4, Square.d3));
        assertEquals(1031680, result.getChildNode(Square.c3, Square.a4));
        assertEquals(1082885, result.getChildNode(Square.c3, Square.d5));
        assertEquals(2644890, result.getChildNode(Square.e4, Square.e3));
        assertEquals(891861, result.getChildNode(Square.c3, Square.a2));
        assertEquals(1784650, result.getChildNode(Square.e4, Square.d4));
        assertEquals(889490, result.getChildNode(Square.c3, Square.b1));
        assertEquals(2805224, result.getChildNode(Square.e4, Square.f3));
        assertEquals(1634937, result.getChildNode(Square.e4, Square.f4));
        assertEquals(1370235, result.getChildNode(Square.c3, Square.e2));
        assertEquals(1140470, result.getChildNode(Square.c3, Square.b5));
        assertEquals(12, result.getMovesCount());
        assertEquals(19134873, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test07() {
        game = getGame("8/6nr/4k3/8/4K3/1RN5/8/8 w - - 0 1");

        PerftResult result = perft.start(game, 6);
        //printForUnitTest(result);

        assertEquals(2212999, result.getChildNode(Square.b3, Square.a3));
        assertEquals(2197875, result.getChildNode(Square.c3, Square.a2));
        assertEquals(3082252, result.getChildNode(Square.e4, Square.d3));
        assertEquals(2254962, result.getChildNode(Square.c3, Square.a4));
        assertEquals(2045379, result.getChildNode(Square.c3, Square.b1));
        assertEquals(2858626, result.getChildNode(Square.b3, Square.b1));
        assertEquals(2304740, result.getChildNode(Square.e4, Square.f4));
        assertEquals(2614597, result.getChildNode(Square.b3, Square.b5));
        assertEquals(2958990, result.getChildNode(Square.b3, Square.b2));
        assertEquals(1747591, result.getChildNode(Square.b3, Square.b7));
        assertEquals(3237330, result.getChildNode(Square.e4, Square.f3));
        assertEquals(541878, result.getChildNode(Square.b3, Square.b6));
        assertEquals(2363756, result.getChildNode(Square.b3, Square.b4));
        assertEquals(2565589, result.getChildNode(Square.c3, Square.e2));
        assertEquals(2327908, result.getChildNode(Square.e4, Square.d4));
        assertEquals(2351411, result.getChildNode(Square.c3, Square.d1));
        assertEquals(3009816, result.getChildNode(Square.e4, Square.e3));
        assertEquals(2724720, result.getChildNode(Square.b3, Square.b8));
        assertEquals(1910432, result.getChildNode(Square.c3, Square.b5));
        assertEquals(2214933, result.getChildNode(Square.c3, Square.d5));
        assertEquals(20, result.getMovesCount());
        assertEquals(47525784, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test08() {
        game = getGame("7b/6nr/b3k3/8/4K3/1RN5/6B1/B7 w - - 0 1");

        PerftResult result = perft.start(game, 6);
        //printForUnitTest(result);

        assertEquals(5843773, result.getChildNode(Square.a1, Square.b2));
        assertEquals(5050601, result.getChildNode(Square.b3, Square.a3));
        assertEquals(6882309, result.getChildNode(Square.b3, Square.b1));
        assertEquals(6387663, result.getChildNode(Square.b3, Square.b2));
        assertEquals(6267046, result.getChildNode(Square.b3, Square.b4));
        assertEquals(5469090, result.getChildNode(Square.b3, Square.b5));
        assertEquals(1187018, result.getChildNode(Square.b3, Square.b6));
        assertEquals(4491445, result.getChildNode(Square.b3, Square.b7));
        assertEquals(7152671, result.getChildNode(Square.b3, Square.b8));
        assertEquals(7746775, result.getChildNode(Square.c3, Square.a2));
        assertEquals(7770428, result.getChildNode(Square.c3, Square.a4));
        assertEquals(7321575, result.getChildNode(Square.c3, Square.b1));
        assertEquals(4895914, result.getChildNode(Square.c3, Square.b5));
        assertEquals(8077076, result.getChildNode(Square.c3, Square.d1));
        assertEquals(8371140, result.getChildNode(Square.c3, Square.d5));
        assertEquals(7758167, result.getChildNode(Square.c3, Square.e2));
        assertEquals(6593738, result.getChildNode(Square.e4, Square.d4));
        assertEquals(10258804, result.getChildNode(Square.e4, Square.e3));
        assertEquals(7603871, result.getChildNode(Square.e4, Square.f3));
        assertEquals(9224144, result.getChildNode(Square.e4, Square.f4));
        assertEquals(6380370, result.getChildNode(Square.g2, Square.f1));
        assertEquals(6088282, result.getChildNode(Square.g2, Square.f3));
        assertEquals(5404578, result.getChildNode(Square.g2, Square.h1));
        assertEquals(1713973, result.getChildNode(Square.g2, Square.h3));
        assertEquals(24, result.getMovesCount());
        assertEquals(153940451, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test09() {
        game = getGame("7b/2q3nr/b3k3/8/4K3/1RN5/2Q3B1/B7 w - - 0 1");

        PerftResult result = perft.start(game, 6);
        //printForUnitTest(result);

        assertEquals(36666051, result.getChildNode(Square.a1, Square.b2));
        assertEquals(36505685, result.getChildNode(Square.b3, Square.a3));
        assertEquals(45539047, result.getChildNode(Square.b3, Square.b1));
        assertEquals(35769596, result.getChildNode(Square.b3, Square.b2));
        assertEquals(43607084, result.getChildNode(Square.b3, Square.b4));
        assertEquals(40009704, result.getChildNode(Square.b3, Square.b5));
        assertEquals(6315178, result.getChildNode(Square.b3, Square.b6));
        assertEquals(35521665, result.getChildNode(Square.b3, Square.b7));
        assertEquals(48484024, result.getChildNode(Square.b3, Square.b8));
        assertEquals(29694396, result.getChildNode(Square.c2, Square.a2));
        assertEquals(37798546, result.getChildNode(Square.c2, Square.b1));
        assertEquals(31810893, result.getChildNode(Square.c2, Square.b2));
        assertEquals(47758518, result.getChildNode(Square.c2, Square.c1));
        assertEquals(43033202, result.getChildNode(Square.c2, Square.d1));
        assertEquals(45442176, result.getChildNode(Square.c2, Square.d2));
        assertEquals(36112497, result.getChildNode(Square.c2, Square.d3));
        assertEquals(39498874, result.getChildNode(Square.c2, Square.e2));
        assertEquals(49103373, result.getChildNode(Square.c2, Square.f2));
        assertEquals(55630531, result.getChildNode(Square.c3, Square.a2));
        assertEquals(56823801, result.getChildNode(Square.c3, Square.a4));
        assertEquals(52560441, result.getChildNode(Square.c3, Square.b1));
        assertEquals(40626002, result.getChildNode(Square.c3, Square.b5));
        assertEquals(56876536, result.getChildNode(Square.c3, Square.d1));
        assertEquals(69666932, result.getChildNode(Square.c3, Square.d5));
        assertEquals(52272762, result.getChildNode(Square.c3, Square.e2));
        assertEquals(36040208, result.getChildNode(Square.e4, Square.d4));
        assertEquals(60167040, result.getChildNode(Square.e4, Square.e3));
        assertEquals(47627374, result.getChildNode(Square.e4, Square.f3));
        assertEquals(46908196, result.getChildNode(Square.g2, Square.f1));
        assertEquals(45260359, result.getChildNode(Square.g2, Square.f3));
        assertEquals(41365439, result.getChildNode(Square.g2, Square.h1));
        assertEquals(8258117, result.getChildNode(Square.g2, Square.h3));
        assertEquals(32, result.getMovesCount());
        assertEquals(1358754247, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test10() {
        game = getGame("8/P3k1p1/8/3p1P1P/p1p1P3/8/1P2K2p/8 w - - 1 1");

        PerftResult result = perft.start(game, 8);
        assertEquals(180835438, result.getChildNode(Square.a7, Square.a8, Piece.KNIGHT_WHITE));
        assertEquals(223920815, result.getChildNode(Square.a7, Square.a8, Piece.BISHOP_WHITE));
        assertEquals(401057318, result.getChildNode(Square.a7, Square.a8, Piece.QUEEN_WHITE));
        assertEquals(397791194, result.getChildNode(Square.a7, Square.a8, Piece.ROOK_WHITE));
        assertEquals(329056510, result.getChildNode(Square.b2, Square.b3));
        assertEquals(210324242, result.getChildNode(Square.b2, Square.b4));
        assertEquals(155566967, result.getChildNode(Square.e2, Square.d1));
        assertEquals(243062794, result.getChildNode(Square.e2, Square.d2));
        assertEquals(161051941, result.getChildNode(Square.e2, Square.e1));
        assertEquals(256602070, result.getChildNode(Square.e2, Square.e3));
        assertEquals(141710258, result.getChildNode(Square.e2, Square.f1));
        assertEquals(261623014, result.getChildNode(Square.e2, Square.f2));
        assertEquals(243019740, result.getChildNode(Square.e2, Square.f3));
        assertEquals(154953476, result.getChildNode(Square.e4, Square.d5));
        assertEquals(138675715, result.getChildNode(Square.e4, Square.e5));
        assertEquals(133296005, result.getChildNode(Square.f5, Square.f6));
        assertEquals(330821471, result.getChildNode(Square.h5, Square.h6));
        assertEquals(17, result.getMovesCount());
        assertEquals(3963368968L, result.getTotalNodes());
    }

    @Test
    @Disabled
    public void test11() {
        game = getGame("r3k2r/8/8/4n3/3N4/8/8/R3K2R w KQkq - 0 1");
        //game.executeMove(Square.a1, Square.a2);
        //game.executeMove(Square.a8, Square.a3);
        //game.executeMove(Square.a2, Square.f2);
        //game.executeMove(Square.a3, Square.e3);

        PerftResult result = perft.start(game, 6);
        printForUnitTest(result);
    }
}
