package net.chesstango.tools;

import net.chesstango.board.Game;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.gardel.pgn.PGN;
import net.chesstango.gardel.pgn.PGNStringDecoder;
import net.chesstango.piazzolla.syzygy.Syzygy;
import net.chesstango.piazzolla.syzygy.SyzygyPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * No pertenece acá
 *
 * @author Mauricio Coria
 */
public class PgnToFENTest {

    private PGNStringDecoder decoder;

    @BeforeEach
    public void settup() {
        decoder = new PGNStringDecoder();
    }


    @Test
    @Disabled
    public void LumbrasGigaBase_OTB_2025() throws IOException {
        Path lumbrasGigaBase_OTB_2025 = Path.of("C:\\java\\projects\\chess\\chess-utils\\testing\\matches\\LumbrasGigaBase\\LumbrasGigaBase_OTB_2025.pgn");
        //Path lumbrasGigaBase_OTB_2025 = Path.of("C:\\java\\projects\\chess\\chess-utils\\testing\\matches\\Balsa_Top10.pgn");

        System.out.println("LumbrasGigaBase_OTB_2025: " + lumbrasGigaBase_OTB_2025.toAbsolutePath());
        Stream<PGN> lumbrasGigaBase_OTB_2025_PGN = decoder.decodePGNs(lumbrasGigaBase_OTB_2025);

        lumbrasGigaBase_OTB_2025_PGN
                .filter(pgn -> {
                    try {
                        return Long.bitCount(Game.from(pgn).getPosition().getAllPositions()) < 6;
                    } catch (RuntimeException e) {
                        System.err.println("Error decoding PGN: " + pgn.toString());
                        return false;
                    }
                })
                .map(PGN::toFEN)
                .map(fenStram ->
                        fenStram.map(Game::from)
                                .filter(game -> Long.bitCount(game.getPosition().getAllPositions()) == 9)
                                .map(Game::getCurrentFEN)
                                .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(System.out::println);
    }

    @Test
    @Disabled
    public void LumbrasGigaBase_Filter() throws IOException {

        Path syzygyPath = Path.of("C:\\java\\projects\\chess\\chess-utils\\books\\syzygy\\3-4-5");
        Syzygy syzygy = Syzygy.open(syzygyPath);

        Path filePath = Paths.get("C:\\java\\projects\\chess\\chess-utils\\testing\\matches\\LumbrasGigaBase\\LumbrasGigaBase_OTB_2025_5_pieces_finalLessThan6.fen");
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.filter(s -> s != null && !s.trim().isEmpty())
                    .map(FEN::of)
                    .filter(fen -> "b".equals(fen.getActiveColor()))          // White turn
                    .filter(fen -> {
                        SyzygyPosition position = SyzygyPosition.from(fen);
                        int wdl = syzygy.tb_probe_wdl(position);
                        return Syzygy.TB_GET_WDL(wdl) == Syzygy.TB_DRAW;     // WINS
                    })
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

    }


}
