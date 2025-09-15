package net.chesstango.tools.epd;

import net.chesstango.engine.Tango;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.gardel.epd.EPD;
import net.chesstango.gardel.epd.EPDDecoder;
import net.chesstango.search.builders.AlphaBetaBuilder;
import net.chesstango.tools.reports.epd.EpdSearchReport;
import net.chesstango.tools.reports.epd.EpdSearchReportModel;
import net.chesstango.tools.reports.evaluation.EvaluationReport;
import net.chesstango.tools.reports.evaluation.EvaluationReportModel;
import net.chesstango.tools.reports.nodes.NodesReport;
import net.chesstango.tools.reports.nodes.NodesReportModel;
import net.chesstango.tools.reports.pv.PrincipalVariationReport;
import net.chesstango.tools.reports.pv.PrincipalVariationReportModel;
import net.chesstango.tools.reports.summary.SummaryModel;
import net.chesstango.tools.reports.summary.SummarySaver;
import net.chesstango.tools.worker.epd.EpdSearch;
import net.chesstango.tools.worker.epd.result.EpdSearchResult;
import net.chesstango.tools.worker.epd.result.EpdSearchResultBuildWithBestMove;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static net.chesstango.tools.epd.Common.*;

/**
 * Esta clase esta destinada a resolver test-positions
 * <p>
 * https://www.chessprogramming.org/Test-Positions
 *
 * @author Mauricio Coria
 */
public class EpdSearchMain implements Runnable {

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

        new EpdSearchMain(epdFiles, depth, timeOut, sessionDirectory).run();
    }

    private final List<Path> epdFiles;
    private final int depth;
    private final int timeOut;
    private final Path sessionDirectory;

    public EpdSearchMain(List<Path> epdFiles, int depth, int timeOut, Path sessionDirectory) {
        this.epdFiles = epdFiles;
        this.depth = depth;
        this.timeOut = timeOut;
        this.sessionDirectory = sessionDirectory;
    }

    @Override
    public void run() {
        EpdSearch epdSearch = new EpdSearch()
                .setSearchSupplier(() -> AlphaBetaBuilder
                        .createDefaultBuilderInstance()
                        // Hasta v0.0.27 se utilizó EvaluatorSEandImp02
                        // (ahora EvaluatorImp04) como evaluador
                        .withGameEvaluator(Evaluator.getInstance())
                        .withStatistics()
                        .build())
                .setDepth(depth)
                .setEpdSearchResultBuilder(new EpdSearchResultBuildWithBestMove());


        if (timeOut > 0) {
            epdSearch.setTimeOut(timeOut);
        }

        epdFiles.forEach(epdFile -> execute(epdSearch, epdFile));
    }

    private void execute(EpdSearch epdSearch, Path suitePath) {
        EPDDecoder reader = new EPDDecoder();

        List<EPD> edpEntries = reader.readEpdFile(suitePath);

        List<EpdSearchResult> epdSearchResults = epdSearch.run(edpEntries);

        report(suitePath, epdSearchResults);
    }

    private void report(Path suitePath, List<EpdSearchResult> epdSearchResults) {
        String suiteName = suitePath.getFileName().toString();

        EpdSearchReportModel epdSearchReportModel = EpdSearchReportModel.collectStatistics(suiteName, epdSearchResults);
        NodesReportModel nodesReportModel = NodesReportModel.collectStatistics(suiteName, epdSearchResults.stream().map(EpdSearchResult::getSearchResult).toList());
        EvaluationReportModel evaluationReportModel = EvaluationReportModel.collectStatistics(suiteName, epdSearchResults.stream().map(EpdSearchResult::getSearchResult).toList());
        PrincipalVariationReportModel principalVariationReportModel = PrincipalVariationReportModel.collectStatics(suiteName, epdSearchResults.stream().map(EpdSearchResult::getSearchResult).toList());
        SummaryModel summaryModel = SummaryModel.collectStatics(SEARCH_SESSION_ID, epdSearchResults, epdSearchReportModel, nodesReportModel, evaluationReportModel, principalVariationReportModel);

        //printReports(System.out, epdSearchReportModel, nodesReportModel, evaluationReportModel);

        //saveReports(sessionDirectory, suiteName, epdSearchReportModel, nodesReportModel, evaluationReportModel, principalVariationReportModel);
        saveSearchSummary(sessionDirectory, suiteName, summaryModel);
    }

    private void saveSearchSummary(Path sessionDirectory, String suiteName, SummaryModel summaryModel) {
        Path searchSummaryPath = sessionDirectory.resolve(String.format("%s.json", suiteName));

        try (PrintStream out = new PrintStream(new FileOutputStream(searchSummaryPath.toFile()), true)) {
            new SummarySaver()
                    .withSearchSummaryModel(summaryModel)
                    .print(out);

            out.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void saveReports(Path sessionDirectory, String suiteName, EpdSearchReportModel epdSearchReportModel, NodesReportModel nodesReportModel, EvaluationReportModel evaluationReportModel, PrincipalVariationReportModel principalVariationReportModel) {
        Path suitePathReport = sessionDirectory.resolve(String.format("%s-report.txt", suiteName));

        try (PrintStream out = new PrintStream(new FileOutputStream(suitePathReport.toFile()), true)) {

            printReports(out, epdSearchReportModel, nodesReportModel, evaluationReportModel, principalVariationReportModel);

            out.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void printReports(PrintStream output, EpdSearchReportModel epdSearchReportModel, NodesReportModel nodesReportModel, EvaluationReportModel evaluationReportModel, PrincipalVariationReportModel principalVariationReportModel) {
        output.printf("Version: %s\n", Tango.ENGINE_VERSION);

        new EpdSearchReport()
                .setReportModel(epdSearchReportModel)
                .printReport(output);

        new NodesReport()
                .setReportModel(nodesReportModel)
                .withCutoffStatistics()
                .withNodesVisitedStatistics()
                .printReport(output);

        new EvaluationReport()
                .setReportModel(evaluationReportModel)
                //.withExportEvaluations()
                .withEvaluationsStatistics()
                .printReport(output);


        new PrincipalVariationReport()
                .setReportModel(principalVariationReportModel)
                .printReport(output);
    }


}
