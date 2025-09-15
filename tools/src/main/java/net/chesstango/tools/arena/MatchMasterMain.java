package net.chesstango.tools.arena;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.board.Game;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.gardel.fen.FENParser;
import net.chesstango.gardel.pgn.PGN;
import net.chesstango.gardel.pgn.PGNStringDecoder;
import net.chesstango.tools.arena.master.MatchProducer;
import net.chesstango.tools.arena.master.MatchResponseCallback;
import net.chesstango.tools.worker.match.MatchRequest;
import net.chesstango.uci.arena.matchtypes.MatchByDepth;
import net.chesstango.uci.arena.matchtypes.MatchType;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class MatchMasterMain implements Runnable {

    private final String rabbitHost;
    private final String matchStore;

    public MatchMasterMain(String rabbitHost, String matchStore) {
        this.rabbitHost = rabbitHost;
        this.matchStore = matchStore;
    }

    public static void main(String[] args) {
        String rabbitHost = "localhost";
        String matchStore = "C:\\java\\projects\\chess\\chess-utils\\testing\\matches";

        new MatchMasterMain(rabbitHost, matchStore).run();
    }

    @Override
    public void run() {
        log.info("Starting");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setSharedExecutor(executorService);

            try (MatchProducer matchProducer = MatchProducer.open(factory)) {

                MatchResponseCallback callback = MatchResponseCallback.open(Path.of(matchStore));

                List<MatchRequest> matchRequests = createMatchRequests(new MatchByDepth(4), getFEN_FromPGN(), true);

                CountDownLatch countDownLatch = new CountDownLatch(matchRequests.size());

                matchProducer.setupCallback(callback.andThen(matchResponse -> countDownLatch.countDown()));

                matchRequests.forEach(matchProducer::publish);

                countDownLatch.await();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Finished");
    }


    private static List<MatchRequest> createMatchRequests(MatchType match, List<FEN> fenList, boolean switchChairs) {
        //String player1 = "class:DefaultTango";
        String player1 = "file:Tango-v1.1.0";
        String player2 = "file:Spike";
        Stream<MatchRequest> result = fenList.stream()
                .map(fen -> new MatchRequest()
                        .setWhiteEngine(player1)
                        .setBlackEngine(player2)
                        .setFen(fen)
                        .setMatchType(match)
                        .setMatchId(UUID.randomUUID().toString())
                );

        if (switchChairs) {
            Stream<MatchRequest> switchStream = fenList.stream()
                    .map(fen -> new MatchRequest()
                            .setWhiteEngine(player2)
                            .setBlackEngine(player1)
                            .setFen(fen)
                            .setMatchType(match)
                            .setMatchId(UUID.randomUUID().toString())
                    );

            result = Stream.concat(result, switchStream);
        }

        return result.toList();
    }


    private static List<FEN> getFEN_FromPGN() {
        //Stream<PGN> pgnStream = new PGNStringDecoder().decodePGNs(MatchMasterMain.class.getClassLoader().getResourceAsStream("Balsa_Top10.pgn"));
        //Stream<PGN> pgnStream = new PGNStringDecoder().decodePGNs(MatchMasterMain.class.getClassLoader().getResourceAsStream("Balsa_Top25.pgn"));
        //Stream<PGN> pgnStream = new PGNStringDecoder().decodePGNs(MatchMasterMain.class.getClassLoader().getResourceAsStream("Balsa_Top50.pgn"));
        Stream<PGN> pgnStream = new PGNStringDecoder().decodePGNs(MatchMasterMain.class.getClassLoader().getResourceAsStream("Balsa_v500.pgn"));
        //Stream<PGN> pgnStream = new PGNStringDecoder().decodePGNs(MatchMasterMain.class.getClassLoader().getResourceAsStream("Balsa_v2724.pgn"));

        return pgnStream
                .map(Game::from)
                .map(Game::getCurrentFEN)
                .toList();
    }


    private static List<FEN> getFEN() {
        List<String> fenList = List.of(FENParser.INITIAL_FEN);
        //List<String> fenList =  List.of("K7/N7/k7/8/3p4/8/N7/8 w - - 0 1", "8/8/8/6B1/8/8/4k3/1K5N b - - 0 1");
        //List<String> fenList =  List.of("1k1r3r/pp6/2P1bp2/2R1p3/Q3Pnp1/P2q4/1BR3B1/6K1 b - - 0 1");
        //List<String> fenList =  List.of(FENDecoder.INITIAL_FEN, "1k1r3r/pp6/2P1bp2/2R1p3/Q3Pnp1/P2q4/1BR3B1/6K1 b - - 0 1");

        return fenList.stream().map(FEN::of).toList();
    }
}
