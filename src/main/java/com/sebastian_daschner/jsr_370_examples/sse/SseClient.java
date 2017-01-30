package com.sebastian_daschner.jsr_370_examples.sse;

import javax.ws.rs.Flow;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.function.Consumer;

public class SseClient {

    private final WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/jersey-test/resources/events-examples/events");
    private SseEventSource eventSource;

    public void connect(Consumer<String> dataConsumer) {
        eventSource = SseEventSource.target(target)
                .open(es -> es.subscribe(new Flow.Subscriber<InboundSseEvent>() {
                    @Override
                    public void onSubscribe(final Flow.Subscription subscription) {
                        System.out.println("new subscription arrived " + subscription);
                    }

                    @Override
                    public void onNext(final InboundSseEvent item) {
                        dataConsumer.accept(item.readData());
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("completed");
                    }
                }));
    }

    public void disconnect() {
        if (eventSource != null)
            eventSource.close();
    }

}
