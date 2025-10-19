package net.chesstango.epd.core.report;


import com.fasterxml.jackson.annotation.JsonProperty;
import net.chesstango.board.representations.move.SimpleMoveEncoder;
import net.chesstango.epd.core.search.EpdSearchResult;
import net.chesstango.reports.detail.evaluation.EvaluationModel;
import net.chesstango.reports.detail.nodes.NodesModel;
import net.chesstango.reports.detail.pv.PrincipalVariationModel;
import net.chesstango.search.SearchResultByDepth;
import net.chesstango.search.SearchResult;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Mauricio Coria
 */
public class SummaryModel {

    @JsonProperty("sessionid")
    String sessionid;

    @JsonProperty("duration")
    long duration;

    @JsonProperty("searches")
    int searches;

    @JsonProperty("success")
    int success;

    @JsonProperty("successRate")
    int successRate;

    @JsonProperty("depthAccuracyAvgPercentageTotal")
    int depthAccuracyPct;

    @JsonProperty("executedMovesTotal")
    long executedMovesTotal;

    @JsonProperty("maxSearchRLevel")
    int maxSearchRLevel;

    @JsonProperty("maxSearchQLevel")
    int maxSearchQLevel;

    @JsonProperty("visitedRNodesTotal")
    long visitedRNodesTotal;

    @JsonProperty("visitedQNodesTotal")
    long visitedQNodesTotal;

    @JsonProperty("visitedNodesTotal")
    long visitedNodesTotal;

    @JsonProperty("cutoffPercentageTotal")
    int cutoffPercentageTotal;

    @JsonProperty("evaluationCounterTotal")
    long evaluationCounterTotal;

    @JsonProperty("evaluationCollisionPercentageTotal")
    int evaluationCollisionPercentageTotal;

    @JsonProperty("pvAccuracyAvgPercentageTotal")
    int pvAccuracyAvgPercentageTotal;

    @JsonProperty("searchDetail")
    List<SearchSummaryModeDetail> searchDetailList = new LinkedList<>();

    public static class SearchSummaryModeDetail {
        @JsonProperty("id")
        public String id;

        @JsonProperty("move")
        public String move;

        @JsonProperty("success")
        public boolean success;

        @JsonProperty("depthMoves")
        public String depthMoves;

        @JsonProperty("depthAccuracyPercentage")
        public int depthAccuracyPercentage;

        @JsonProperty("pv")
        public String pv;

        @JsonProperty("pvAccuracyPercentage")
        public int pvAccuracyPercentage;

        @JsonProperty("evaluation")
        public int evaluation;
    }


    public static SummaryModel collectStatics(String sessionId,
                                              List<EpdSearchResult> epdSearchResults,
                                              EpdSearchReportModel epdSearchReportModel,
                                              NodesModel nodesReportModel,
                                              EvaluationModel evaluationReportModel,
                                              PrincipalVariationModel principalVariationReportModel) {

        SummaryModel model = new SummaryModel();

        model.sessionid = sessionId;
        model.duration = epdSearchReportModel.duration;
        model.searches = epdSearchReportModel.searches;

        model.success = epdSearchReportModel.success;
        model.successRate = epdSearchReportModel.successRate;
        model.depthAccuracyPct = epdSearchReportModel.depthAccuracyPct;

        model.maxSearchRLevel = nodesReportModel.maxSearchRLevel;
        model.maxSearchQLevel = nodesReportModel.maxSearchQLevel;

        model.visitedRNodesTotal = nodesReportModel.visitedRNodesTotal;
        model.visitedQNodesTotal = nodesReportModel.visitedQNodesTotal;
        model.visitedNodesTotal = nodesReportModel.visitedNodesTotal;
        model.executedMovesTotal = nodesReportModel.executedMovesTotal;
        model.cutoffPercentageTotal = nodesReportModel.cutoffPercentageTotal;
        model.evaluationCounterTotal = evaluationReportModel.evaluationCounterTotal;
        model.evaluationCollisionPercentageTotal = evaluationReportModel.evaluationCollisionPercentageTotal;
        model.pvAccuracyAvgPercentageTotal = principalVariationReportModel.pvAccuracyAvgPercentageTotal;

        Map<String, PrincipalVariationModel.PrincipalVariationReportModelDetail> pvMap = new HashMap<>();
        principalVariationReportModel.moveDetails.forEach(pvMoveDetail -> pvMap.put(pvMoveDetail.id, pvMoveDetail));

        SimpleMoveEncoder simpleMoveEncoder = new SimpleMoveEncoder();

        epdSearchResults.stream().map(epdSearchResult -> {
            SearchSummaryModeDetail searchSummaryModeDetail = new SearchSummaryModeDetail();
            SearchResult searchResult = epdSearchResult.getSearchResult();
            PrincipalVariationModel.PrincipalVariationReportModelDetail pvDetail = pvMap.get(epdSearchResult.getEpd().getId());

            searchSummaryModeDetail.id = epdSearchResult.getEpd().getId();
            searchSummaryModeDetail.move = epdSearchResult.getBestMoveFound();
            searchSummaryModeDetail.success = epdSearchResult.isSearchSuccess();
            searchSummaryModeDetail.depthMoves = searchResult.getSearchResultByDepths().stream().map(SearchResultByDepth::getBestMove).map(simpleMoveEncoder::encode).toList().toString();
            searchSummaryModeDetail.depthAccuracyPercentage = epdSearchResult.getDepthAccuracyPct();
            searchSummaryModeDetail.pv = pvDetail.principalVariation;
            searchSummaryModeDetail.pvAccuracyPercentage = pvDetail.pvAccuracyPercentage;
            searchSummaryModeDetail.evaluation = searchResult.getBestEvaluation();
            return searchSummaryModeDetail;
        }).forEach(model.searchDetailList::add);


        return model;
    }
}


