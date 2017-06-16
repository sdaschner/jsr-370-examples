package com.sebastian_daschner.jsr_370_examples.rx;

import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class RxClient {

    private static final int NUMBER_CALLS = 10;
    private final WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/jsr-370-examples/resources/slow");

    public void testSimple() {
        final OptionalDouble average = IntStream.range(0, NUMBER_CALLS).parallel()
                .mapToObj(i -> target.request(MediaType.APPLICATION_JSON_TYPE)
                        .get(JsonObject.class))
                .mapToInt(o -> o.getInt("total"))
                .average();
        System.out.println("average = " + average.getAsDouble());
    }

    public void testCompletionStage() {
        final Executor executor = Executors.newCachedThreadPool();

        final OptionalDouble average = IntStream.range(0, NUMBER_CALLS).parallel()
                .map(i ->
                        CompletableFuture.supplyAsync(() -> target
                                .request(MediaType.APPLICATION_JSON_TYPE)
                                .get(JsonObject.class), executor)
                                .thenApply(o -> o.getInt("total"))
                                .join()
                ).average();
        System.out.println("average = " + average.getAsDouble());
    }

    public void testCompletionStageWithoutStreams() {
        final Executor executor = Executors.newCachedThreadPool();

        final CompletableFuture<Integer>[] futures = new CompletableFuture[NUMBER_CALLS];
        for (int i = 0; i < NUMBER_CALLS; i++) {
            futures[i] = CompletableFuture.supplyAsync(() -> target.request(MediaType.APPLICATION_JSON_TYPE).get(JsonObject.class), executor)
                    .thenApply(o -> o.getInt("total"));
        }

        int average = 0;

        for (int i = 0; i < NUMBER_CALLS; i++) {
            average += futures[i].join();
        }
        average /= NUMBER_CALLS;

        System.out.println("average = " + average);
    }

    public void testRx() {
        final ExecutorService executor = Executors.newCachedThreadPool();

        final OptionalDouble average = IntStream.range(0, NUMBER_CALLS).parallel()
                .map(i -> target.request(MediaType.APPLICATION_JSON_TYPE)
                        .rx(executor)
                        .get(JsonObject.class)
                        .thenApply(o -> o.getInt("total"))
                        .toCompletableFuture().join()
                ).average();

        System.out.println("average = " + average.getAsDouble());
    }

}
