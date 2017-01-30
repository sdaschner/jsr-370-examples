package com.sebastian_daschner.jsr_370_examples;

import com.sebastian_daschner.jsr_370_examples.sse.SseClient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

@Ignore
public class SseClientTest {

    @Test
    public void testSseCall() {
        final SseClient client = new SseClient();
        client.connect(s -> System.out.println("got message! " + s));
        LockSupport.parkNanos(5_000_000_000L);
        client.disconnect();
    }

}
