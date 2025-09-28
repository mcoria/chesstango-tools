module net.chesstango.tools {
    exports net.chesstango.tools;
    exports net.chesstango.tools.tuning.fitnessfunctions;
    exports net.chesstango.tools.tuning.geneticproviders;
    exports net.chesstango.tools.tuning.factories;
    exports net.chesstango.tools.tuning;

    requires net.chesstango.reports;
    requires net.chesstango.board;
    requires net.chesstango.engine;
    requires net.chesstango.evaluation;
    requires net.chesstango.gardel;
    requires net.chesstango.piazzolla;
    requires net.chesstango.search;
    requires net.chesstango.arena.core;
    requires net.chesstango.arena.worker;
    requires net.chesstango.arena.master;
    requires net.chesstango.uci.engine;
    requires net.chesstango.uci.gui;
    requires net.chesstango.mbeans;
    requires net.chesstango.epd.core;
    requires net.chesstango.epd.worker;

    requires com.rabbitmq.client;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires io.jenetics.base;
    requires org.apache.commons.pool2;
    requires org.apache.commons.cli;
    requires py4j;

    requires static lombok;

    opens net.chesstango.tools.epd.report to com.fasterxml.jackson.databind;
}