package com.sebastian_daschner.jsr_370_examples.sse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.time.Instant;

@Path("sse")
public class SseResource {

    @Inject
    Sse sse;

    private SseBroadcaster sseBroadcaster;

    @PostConstruct
    private void init() {
        sseBroadcaster = sse.newBroadcaster();
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void helloStream(@Context SseEventSink eventSink) {
        sseBroadcaster.subscribe(eventSink);
    }

    @Schedule
    public void sentEvent() {
        final OutboundSseEvent event = sse.newEvent("hello world, " + Instant.now());
        sseBroadcaster.broadcast(event);
    }

    @PreDestroy
    public void close() {
        sseBroadcaster.close();
    }

}
