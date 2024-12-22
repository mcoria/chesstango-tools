package net.chesstango.uci.gui;

import net.chesstango.uci.engine.engine.UciTango;

/**
 * @author Mauricio Coria
 */
public class EngineControllerTango extends EngineControllerAbstract {
    private final UciTango uciTango;

    public EngineControllerTango(UciTango uciTango) {
        super(uciTango);
        this.uciTango = uciTango;
    }

    @Override
    public void accept(EngineControllerVisitor engineControllerVisitor) {
        engineControllerVisitor.visit(uciTango);
    }
}
