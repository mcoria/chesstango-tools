module net.chesstango.epd.core {
    exports net.chesstango.core;

    requires net.chesstango.gardel;
    requires net.chesstango.search;
    requires net.chesstango.board;

    requires org.slf4j;
    requires static lombok;
}