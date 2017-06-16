package com.sebastian_daschner.jsr_370_examples;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.junit.Test;

public class EventsResourceIT {

    @Test
    public void testGetIt() {
//        ResourceConfig resourceConfig = ResourceConfig.forApplicationClass(JAXRSConfiguration.class, singleton(EventsResource.class));
//        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8080"), resourceConfig);
        final GrizzlyHttpContainer container = ContainerFactory.createContainer(GrizzlyHttpContainer.class, new JAXRSConfiguration());
        container.start();
        try {
//            server.start();
            System.out.println("Press any key to stop the service...");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        server.shutdownNow();
    }

}
