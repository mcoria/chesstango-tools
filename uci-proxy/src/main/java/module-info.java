module net.chesstango.uci.proxy {
    exports net.chesstango.uci.proxy;

    requires net.chesstango.uci.protocol;
    requires org.slf4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires static lombok;
}