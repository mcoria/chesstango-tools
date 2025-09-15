package net.chesstango.tools.worker.epd.result;

import net.chesstango.gardel.epd.EPD;
import net.chesstango.search.SearchResult;

import java.util.function.BiFunction;

/**
 * @author Mauricio Coria
 */
public interface EpdSearchResultBuilder extends BiFunction<EPD, SearchResult, EpdSearchResult> {
}
