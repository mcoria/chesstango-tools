package net.chesstango.tools.worker.match;

import net.chesstango.uci.arena.ControllerFactory;
import net.chesstango.uci.gui.Controller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mauricio Coria
 */
class ControllerProvider implements AutoCloseable {

    private final Path catalogDirectory;

    private final Map<String, Controller> controllers = new HashMap<>();

    private ControllerProvider(Path catalogDirectory) {
        this.catalogDirectory = catalogDirectory;
    }

    static ControllerProvider create(String catalogDirectoryString) {
        Path catalogDirectory = Path.of(catalogDirectoryString);
        if (!catalogDirectory.toFile().exists()) {
            throw new RuntimeException("Catalog directory not found: " + catalogDirectoryString);
        }
        return new ControllerProvider(catalogDirectory);
    }

    Controller getController(String engineName) {
        return controllers.computeIfAbsent(engineName, this::openController);
    }

    @Override
    public void close() {
        controllers.values().forEach(controller -> {
            controller.stopEngine();
            controller.close();
        });
    }

    private Controller openController(String engineName) {
        Path enginePath = catalogDirectory.resolve(String.format("%s.json", engineName));
        if (!enginePath.toFile().exists()) {
            throw new RuntimeException("Engine not found: " + engineName);
        }

        Controller controller = ControllerFactory.createProxyController(enginePath);

        controller.startEngine();

        return controller;
    }

}
