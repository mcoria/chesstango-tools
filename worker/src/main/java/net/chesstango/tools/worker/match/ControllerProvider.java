package net.chesstango.tools.worker.match;

import net.chesstango.uci.arena.ControllerFactory;
import net.chesstango.uci.gui.Controller;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mauricio Coria
 */
public class ControllerProvider implements AutoCloseable {

    private final Path catalogDirectory;

    private final Map<String, Controller> controllers = new HashMap<>();

    public static ControllerProvider create(Path catalogDirectory) {
        return new ControllerProvider(catalogDirectory);
    }

    private ControllerProvider(Path catalogDirectory) {
        this.catalogDirectory = catalogDirectory;
    }

    public Controller getController(String engineName) {
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
        Path enginePath = catalogDirectory.resolve(engineName);
        if (!enginePath.toFile().exists()) {
            throw new RuntimeException("Engine not found: " + engineName);
        }

        Controller controller = ControllerFactory.createProxyController(enginePath);

        controller.startEngine();

        return controller;
    }

}
