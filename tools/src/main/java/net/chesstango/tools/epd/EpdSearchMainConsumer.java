package net.chesstango.tools.epd;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.epd.EpdSearchResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.chesstango.tools.epd.Common.createSessionDirectory;


/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchMainConsumer implements Runnable {

    public static void main(String[] args) throws Exception {
        String rabbitHost = args[0];

        String directory = args[1];

        System.out.printf("directory={%s}\n", directory);

        Path suiteDirectory = Path.of(directory);
        if (!Files.exists(suiteDirectory) || !Files.isDirectory(suiteDirectory)) {
            throw new RuntimeException("Directory not found: " + directory);
        }

        new EpdSearchMainConsumer(rabbitHost, suiteDirectory).run();
    }

    private final String rabbitHost;
    private final Path suiteDirectory;

    public EpdSearchMainConsumer(String rabbitHost, Path suiteDirectory) {
        if (rabbitHost == null) {
            throw new IllegalArgumentException("rabbitHost and enginesCatalog must be provided");
        }
        this.rabbitHost = rabbitHost;
        this.suiteDirectory = suiteDirectory;
    }

    @Override
    public void run() {
        log.info("To exit press CTRL+C");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setSharedExecutor(executorService);

            log.info("Connecting to RabbitMQ");
            try (EpdSearchConsumer epdSearchConsumer = new EpdSearchConsumer(factory)) {

                log.info("Connected to RabbitMQ");

                epdSearchConsumer.setupQueueConsumer(this::accept);

                log.info("Waiting for EpdSearchRequest");

                Thread.sleep(Long.MAX_VALUE);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Done");
    }


    public void accept(EpdSearchResponse epdSearchResponse) {
        Path sessionDirectory = createSessionDirectory(suiteDirectory, epdSearchResponse.getSessionId());

        EpdSearchReportSaver epdSearchReportSaver = new EpdSearchReportSaver(sessionDirectory);

        log.info("Saving report for {}", epdSearchResponse.getSearchId());

        epdSearchReportSaver.saveReport(epdSearchResponse.getSearchId(), epdSearchResponse.getEpdSearchResults());
    }
}
