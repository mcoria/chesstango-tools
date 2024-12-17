package net.chesstango.uci.arena.gui;

import net.chesstango.engine.Tango;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.search.DefaultSearch;
import net.chesstango.search.Search;
import net.chesstango.uci.engine.UciTango;
import net.chesstango.uci.proxy.UciProxy;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Mauricio Coria
 */
public class EngineControllerFactory {
    public static EngineController createProxyController(String proxyName, Consumer<UciProxy> fnProxySetup) {
        UciProxy proxy = new UciProxy(ProxyConfigLoader.loadEngineConfig(proxyName));
        if (fnProxySetup != null) {
            fnProxySetup.accept(proxy);
        }
        return new EngineControllerImp(proxy);
    }

    /**
     * Tango without any customization
     * @return
     */
    public static EngineController createTangoController() {
        return new EngineControllerImp(new UciTango());
    }

    /**
     * Tango with search customization
     * @return
     */
    public static EngineController createTangoControllerWithSearch(Supplier<Search> searchMoveSupplier) {
        Search search = searchMoveSupplier.get();

        return new EngineControllerImp(new UciTango(new Tango(searchMoveSupplier.get())))
                .overrideEngineName(search.getClass().getSimpleName());
    }

    /**
     * Tango with evaluator customization
     * @return
     */
    public static EngineController createTangoControllerWithEvaluator(Supplier<Evaluator> gameEvaluatorSupplier) {
        Evaluator evaluator = gameEvaluatorSupplier.get();

        Search search = new DefaultSearch(evaluator);

        return new EngineControllerImp(new UciTango(new Tango(search)))
                .overrideEngineName(evaluator.getClass().getSimpleName());
    }

}
