package net.chesstango.tools.tuning.fitnessfunctions;

import lombok.Setter;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.gardel.epd.EPD;
import net.chesstango.gardel.epd.EPDDecoder;
import net.chesstango.search.Search;
import net.chesstango.search.SearchResult;
import net.chesstango.epd.core.EpdSearch;
import net.chesstango.epd.core.EpdSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Mauricio Coria
 */
public abstract class FitnessByEpdAbstract implements FitnessFunction {
    private static final Logger logger = LoggerFactory.getLogger(FitnessByEpdAbstract.class);

    protected final List<EPD> edpEntries = new LinkedList<>();

    @Setter
    protected List<String> epdFiles;

    @Setter
    protected int depth;

    protected abstract Supplier<Search> createSearchSupplier(Supplier<Evaluator> gameEvaluatorSupplier);

    protected abstract long getPoints(EPD epd, SearchResult searchResult);


    public FitnessByEpdAbstract(List<String> epdFiles, int depth) {
        this.epdFiles = epdFiles;
        this.depth = depth;
    }

    @Override
    public long fitness(Supplier<Evaluator> gameEvaluatorSupplier) {
        EpdSearch epdSearch = new EpdSearch();

        epdSearch.setDepth(depth);

        epdSearch.setSearchSupplier(createSearchSupplier(gameEvaluatorSupplier));

        List<EpdSearchResult> epdSearchResults = epdSearch.run(edpEntries);

        return epdSearchResults
                .stream()
                .mapToLong(epdSearchResult -> getPoints(epdSearchResult.getEpd(), epdSearchResult.getSearchResult()))
                .sum();
    }

    @Override
    public void start() {
        EPDDecoder reader = new EPDDecoder();

        epdFiles.forEach(fileName -> edpEntries.addAll(reader.readEpdFile(fileName)));
    }

    @Override
    public void stop() {
    }

}
