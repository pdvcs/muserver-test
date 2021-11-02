package net.pdutta.sandbox;

import io.muserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper over local-only web server.
 * We only take requests from localhost, not over the network
 */
public class LocalEndpoint {

    public LocalEndpoint() {
        handler = new ShutdownHandler();
        serverBuilder = MuServerBuilder.httpServer()
                .withHttpPort(11110)
                .withInterface("127.0.0.1")
                .addShutdownHook(true)
                .addHandler(Method.GET, "/", (request, response, pathParams) -> response.write("Hello, world\n"))
                .addHandler(Method.POST, "/shutdown", handler);
    }

    public void start(IEndpointStopper stopper) {
        serverRef = serverBuilder.start();
        handler.addListener(stopper, serverRef);
        log.info("started local-only server at {}", serverRef.uri());
    }


    MuServer serverRef = null;
    MuServerBuilder serverBuilder;
    ShutdownHandler handler;
    Logger log = LoggerFactory.getLogger(LocalEndpoint.class);
}

class ShutdownHandler implements RouteHandler {
    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams) {
        response.contentType("text/plain");
        response.write("shutting down local endpoint...\n");
        // clean-up / close resources
        emitStop();
    }

    public void addListener(IEndpointStopper stopper, MuServer server) {
        stoppers.add(stopper);
        this.server = server;
    }

    public void emitStop() {
        for (IEndpointStopper stopper : stoppers) {
            stopper.stop(server);
        }
    }

    private MuServer server;
    private final List<IEndpointStopper> stoppers = new ArrayList<>();
}
