module net.chesstango.tools {
    exports net.chesstango.tools;
    exports net.chesstango.tools.tuning.fitnessfunctions;
    exports net.chesstango.tools.tuning.geneticproviders;
    exports net.chesstango.tools.tuning.factories;
    exports net.chesstango.tools.tuning;
    exports net.chesstango.tools.search.reports.pv;
    exports net.chesstango.tools.search.reports.nodes;
    exports net.chesstango.tools.search.reports.evaluation;
    exports net.chesstango.tools.epdfilters;

    requires net.chesstango.board;
    requires net.chesstango.engine;
    requires net.chesstango.evaluation;
    requires net.chesstango.gardel;
    requires net.chesstango.piazzolla;
    requires net.chesstango.search;
    requires net.chesstango.uci.arena;
    requires net.chesstango.uci.engine;
    requires net.chesstango.uci.gui;

    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires io.jenetics.base;
    requires org.apache.commons.pool2;
    requires org.apache.commons.cli;
    requires py4j;

    requires static lombok;

    opens net.chesstango.tools.search.reports.summary to com.fasterxml.jackson.databind;
}