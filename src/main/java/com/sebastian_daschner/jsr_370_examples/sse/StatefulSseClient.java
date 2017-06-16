package com.sebastian_daschner.jsr_370_examples.sse;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;
import java.util.function.Consumer;

public class StatefulSseClient {

    private final WebTarget target = ClientBuilder.newClient().target("...");
    private final Consumer<String> dataConsumer;
    private String lastEventId;
    private SseEventSource eventSource;

    public StatefulSseClient(Consumer<String> dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    public void start() {
        eventSource = target.request(MediaType.SERVER_SENT_EVENTS)
                .header(HttpHeaders.LAST_EVENT_ID_HEADER, lastEventId)
                .get(SseEventSource.class);

        eventSource.subscribe(event -> {
            lastEventId = event.getId();
            dataConsumer.accept(event.readData());
        });

        eventSource.open();
    }

    public void stop() {
        if (eventSource != null && eventSource.isOpen())
            eventSource.close();
    }

}
