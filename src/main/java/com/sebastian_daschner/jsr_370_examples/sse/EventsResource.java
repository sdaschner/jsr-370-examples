package com.sebastian_daschner.jsr_370_examples.sse;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("events-examples")
@Singleton
public class EventsResource {

    @Inject
    SseContext sseContext;

    private SseBroadcaster sseBroadcaster;
    private int lastEventId;
    private List<String> messages = new ArrayList<>();

    @PostConstruct
    public void initSse() {
        sseBroadcaster = sseContext.newBroadcaster();

        sseBroadcaster.onException((o, e) -> {
            System.err.println("Got exception while handling output " + o);
            e.printStackTrace();
        });

        sseBroadcaster.onClose(o -> System.out.println("Closing output " + o));

    }

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Lock(LockType.READ)
    public SseEventOutput itemEvents(@HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") final int lastEventId) {
        final SseEventOutput eventOutput = sseContext.newOutput();

        if (lastEventId >= 0) {
            // replay messages
            try {
                for (int i = lastEventId; i < messages.size(); i++) {
                    eventOutput.onNext(createEvent(messages.get(i), i + 1));
                }
            } catch (Exception e) {
                throw new InternalServerErrorException("Could not replay messages ", e);
            }
        }

        sseBroadcaster.subscribe(eventOutput);
        return eventOutput;
    }

    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    @Lock(LockType.WRITE)
    public void newMessage() {
        final String message = "It's now: " + LocalDateTime.now();
        messages.add(message);

        final OutboundSseEvent event = createEvent(message, ++lastEventId);

        sseBroadcaster.broadcast(event);
    }

    private OutboundSseEvent createEvent(final String message, final int id) {
        return sseContext.newEvent().id(String.valueOf(id)).data(message).build();
    }

}
