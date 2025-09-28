module net.chesstango.epd.core {
    exports net.chesstango.epd.core;
    exports net.chesstango.epd.core.report;

    requires net.chesstango.gardel;
    requires net.chesstango.search;
    requires net.chesstango.board;

    requires org.slf4j;
    requires static lombok;
    requires net.chesstango.reports;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens net.chesstango.epd.core.report to com.fasterxml.jackson.databind;
}