package net.pdutta.sandbox;

import io.muserver.MuServer;

public class App implements IEndpointStopper {
    public static void main(String[] args) {
        new App().start();
    }

    public void start() {
        System.out.println("App.start(): starting server");
        LocalEndpoint endpoint = new LocalEndpoint();
        endpoint.start(this);
    }

    @Override
    public void stop(MuServer muServer) {
        System.out.println("App.stop(): stopping server");
        muServer.stop();
    }
}
