package com.sebastian_daschner.jsr_370_examples.sse;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventInput;
import java.io.IOException;
import java.util.function.Consumer;

public class StatefulSseClient {

    private final WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/jersey-test/resources/events-examples/events");
    private final Consumer<String> dataConsumer;
    private String lastEventId;
    private SseEventInput eventInput;

    public StatefulSseClient(Consumer<String> dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    public void start() {
        eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                .header(HttpHeaders.LAST_EVENT_ID_HEADER, lastEventId)
                .get(SseEventInput.class);

        new Thread(() -> {
            while (!eventInput.isClosed()) {
                final InboundSseEvent event = eventInput.read();
                if (event != null) {
                    lastEventId = event.getId();
                    dataConsumer.accept(event.readData());
                }
            }
        }).start();
    }

    public void stop() {
        if (eventInput != null && !eventInput.isClosed())
            try {
                eventInput.close();
            } catch (IOException e) {
                // suppress
            }
    }

}
