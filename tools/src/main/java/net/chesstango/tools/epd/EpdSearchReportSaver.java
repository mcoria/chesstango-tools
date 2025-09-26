package net.chesstango.tools.epd;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.engine.Tango;
import net.chesstango.epd.core.EpdSearchResult;
import net.chesstango.tools.reports.epd.EpdSearchReport;
import net.chesstango.tools.reports.epd.EpdSearchReportModel;
import net.chesstango.tools.reports.evaluation.EvaluationReport;
import net.chesstango.tools.reports.evaluation.EvaluationReportModel;
import net.chesstango.tools.reports.nodes.NodesReport;
import net.chesstango.tools.reports.nodes.NodesReportModel;
import net.chesstango.tools.reports.pv.PrincipalVariationReport;
import net.chesstango.tools.reports.pv.PrincipalVariationReportModel;
import net.chesstango.tools.reports.summary.SummaryModel;
import net.chesstango.tools.reports.summary.SummaryPrinter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import static net.chesstango.tools.epd.Common.SESSION_DATE;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchReportSaver {
    private final Path sessionDirectory;

    public EpdSearchReportSaver(Path sessionDirectory) {
        this.sessionDirectory = sessionDirectory;
    }

    public void saveReport(String suiteName, List<EpdSearchResult> epdSearchResults) {
        EpdSearchReportModel epdSearchReportModel = EpdSearchReportModel.collectStatistics(suiteName, epdSearchResults);
        NodesReportModel nodesReportModel = NodesReportModel.collectStatistics(suiteName, epdSearchResults.stream().map(EpdSearchResult::getSearchResult).toList());
        EvaluationReportModel evaluationReportModel = EvaluationReportModel.collectStatistics(suiteName, epdSearchResults.stream().map(EpdSearchResult::getSearchResult).toList());
        PrincipalVariationReportModel principalVariationReportModel = PrincipalVariationReportModel.collectStatics(suiteName, epdSearchResults.stream().map(EpdSearchResult::getSearchResult).toList());
        SummaryModel summaryModel = SummaryModel.collectStatics(SESSION_DATE, epdSearchResults, epdSearchReportModel, nodesReportModel, evaluationReportModel, principalVariationReportModel);

        saveReports(suiteName, epdSearchReportModel, nodesReportModel, evaluationReportModel, principalVariationReportModel);

        saveSearchSummary(suiteName, summaryModel);
    }

    private void saveSearchSummary(String suiteName, SummaryModel summaryModel) {
        Path searchSummaryPath = sessionDirectory.resolve(String.format("%s.json", suiteName));

        try (PrintStream out = new PrintStream(new FileOutputStream(searchSummaryPath.toFile()), true)) {
            new SummaryPrinter()
                    .withSearchSummaryModel(summaryModel)
                    .print(out);

            out.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void saveReports(String suiteName, EpdSearchReportModel epdSearchReportModel, NodesReportModel nodesReportModel, EvaluationReportModel evaluationReportModel, PrincipalVariationReportModel principalVariationReportModel) {
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
