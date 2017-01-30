package com.sebastian_daschner.jsr_370_examples.rx;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

@Path("slow")
@Produces(MediaType.APPLICATION_JSON)
public class SlowResource {

    @GET
    public JsonObject get() {
        LockSupport.parkNanos(2_000_000_000L);
        return Json.createObjectBuilder().add("total", new Random().nextInt(10)).build();
    }

}
