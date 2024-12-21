package net.chesstango.uci.arena.gui;

import net.chesstango.uci.engine.engine.UciTango;

/**
 * @author Mauricio Coria
 */
public class EngineControllerTango extends EngineControllerImp {
    private final UciTango uciTango;

    public EngineControllerTango(UciTango uciTango) {
        super(uciTango);
        this.uciTango = uciTango;
    }

    @Override
    public void accept(ServiceVisitor serviceVisitor) {
        serviceVisitor.visit(uciTango);
    }
}
