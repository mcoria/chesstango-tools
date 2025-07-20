package net.chesstango.tools.search;

import net.chesstango.board.moves.Move;
import net.chesstango.gardel.epd.EPD;
import net.chesstango.search.SearchResult;
import net.chesstango.search.SearchResultByDepth;

import java.util.List;

/**
 * @author Mauricio Coria
 */
public class EpdSearchResultBuildWithBestMove implements EpdSearchResultBuilder {

    @Override
    public EpdSearchResult apply(EPD epd, SearchResult searchResult) {
        Move bestMove = searchResult.getBestMove();

        return new EpdSearchResult(epd, searchResult)
                .setSearchSuccess(epd.isMoveSuccess(bestMove.coordinateEncoding()))
                .setBestMoveFound(bestMove.coordinateEncoding())
                .setDepthAccuracyPct(calculateAccuracy(epd, searchResult.getSearchResultByDepths()));
    }


    private int calculateAccuracy(EPD epd, List<SearchResultByDepth> searchResultByDepths) {
        if (!searchResultByDepths.isEmpty()) {
            long successCounter = searchResultByDepths
                    .stream()
                    .map(SearchResultByDepth::getBestMove)
                    .map(Move::coordinateEncoding)
                    .filter(epd::isMoveSuccess)
                    .count();
            return (int) (successCounter * 100 / searchResultByDepths.size());
        }
        return 0;
    }
}
