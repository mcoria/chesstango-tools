package net.chesstango.tools.epd;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.gardel.epd.EPD;
import net.chesstango.gardel.epd.EPDDecoder;
import net.chesstango.tools.epd.master.EpdSearchProducer;
import net.chesstango.tools.epd.master.EpdSearchResponseCallback;
import net.chesstango.tools.worker.epd.EpdSearchRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.chesstango.tools.epd.Common.createSessionDirectory;
import static net.chesstango.tools.epd.Common.getEpdFiles;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchMasterMain implements Runnable {
    /**
     * Parametros
     * 1. Depth
     * 2. TimeOut in milliseconds
     * 3. Directorio donde se encuentran los archivos de posicion
     * 4. Filtro de archivos
     * <p>
     * Ejemplo:
     * 4 500 C:\java\projects\chess\chess-utils\testing\positions\database "(mate-[wb][123].epd|Bratko-Kopec.epd|wac-2018.epd|STS*.epd)"
     *
     * @param args
     */
    public static void main(String[] args) {
        int depth = Integer.parseInt(args[0]);

        int timeOut = Integer.parseInt(args[1]);

        String directory = args[2];

        String filePattern = args[3];

        System.out.printf("depth={%d}; timeOut={%d}; directory={%s}; filePattern={%s}\n", depth, timeOut, directory, filePattern);

        Path suiteDirectory = Path.of(directory);
        if (!Files.exists(suiteDirectory) || !Files.isDirectory(suiteDirectory)) {
            throw new RuntimeException("Directory not found: " + directory);
        }

        List<Path> epdFiles = getEpdFiles(suiteDirectory, filePattern);

        Path sessionDirectory = createSessionDirectory(suiteDirectory, depth);

        new EpdSearchMasterMain(epdFiles, depth, timeOut, sessionDirectory).run();
    }

    private final String rabbitHost;

    private final List<Path> epdFiles;
    private final int depth;
    private final int timeOut;
    private final Path sessionDirectory;

    public EpdSearchMasterMain(List<Path> epdFiles, int depth, int timeOut, Path sessionDirectory) {
        this.epdFiles = epdFiles;
        this.depth = depth;
        this.timeOut = timeOut;
        this.sessionDirectory = sessionDirectory;
        this.rabbitHost = "localhost";
    }

    @Override
    public void run() {
        log.info("Starting");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setSharedExecutor(executorService);

            try (EpdSearchProducer epdSearchProducer = EpdSearchProducer.open(factory)) {

                EpdSearchResponseCallback callback = new EpdSearchResponseCallback(sessionDirectory);

                List<EpdSearchRequest> epdSearchRequests = createEpdSearchRequests();

                CountDownLatch countDownLatch = new CountDownLatch(epdSearchRequests.size());

                epdSearchProducer.setupCallback(callback.andThen(matchResponse -> countDownLatch.countDown()));

                epdSearchRequests.forEach(epdSearchProducer::publish);

                countDownLatch.await();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Finished");
    }


    private List<EpdSearchRequest> createEpdSearchRequests() {
        List<EpdSearchRequest> epdSearchRequests = new LinkedList<>();

        EPDDecoder reader = new EPDDecoder();
        for (Path epdFile : epdFiles) {
            List<EPD> edpEntries = reader.readEpdFile(epdFile);

            EpdSearchRequest epdSearchRequest = new EpdSearchRequest();

            epdSearchRequest.setSearchId(getFileName(epdFile.getFileName().toString()));
            epdSearchRequest.setEpdList(edpEntries);
            epdSearchRequest.setDepth(depth);
            epdSearchRequest.setTimeOut(timeOut);

            epdSearchRequests.add(epdSearchRequest);
        }
        return epdSearchRequests;
    }

    public static String getFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
