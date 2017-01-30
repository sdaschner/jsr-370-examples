package com.sebastian_daschner.jsr_370_examples;

import com.sebastian_daschner.jsr_370_examples.sse.StatefulSseClient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

@Ignore
public class StatefulSseClientTest {

    @Test
    public void testSseCall() {
        final StatefulSseClient client = new StatefulSseClient(s -> System.out.println("got message! " + s));
        client.start();
        LockSupport.parkNanos(5_000_000_000L);
        client.stop();
        LockSupport.parkNanos(5_000_000_000L);
        client.start();
        LockSupport.parkNanos(5_000_000_000L);
        client.stop();
    }

}
