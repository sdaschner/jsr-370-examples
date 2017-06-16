package com.sebastian_daschner.jsr_370_examples.sse;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.util.function.Consumer;

public class SseClient {

    private final WebTarget target = ClientBuilder.newClient().target("...");
    private SseEventSource eventSource;

    public void connect(Consumer<String> dataConsumer) {
        eventSource = SseEventSource.target(target).build();
        eventSource.subscribe(
                subscription -> System.out.println("new subscription arrived " + subscription),
                item -> dataConsumer.accept(item.readData()),
                Throwable::printStackTrace,
                () -> System.out.println("completed"));

        eventSource.open();
    }

    public void disconnect() {
        if (eventSource != null)
            eventSource.close();
    }

}
