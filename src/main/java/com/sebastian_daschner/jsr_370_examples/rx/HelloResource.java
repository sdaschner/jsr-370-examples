package com.sebastian_daschner.jsr_370_examples.rx;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("hello")
@Stateless
public class HelloResource {

    @GET
    @Asynchronous
    public void helloAsync(@Suspended AsyncResponse asyncResponse) {
        // ...
        asyncResponse.resume("hello");
    }

}
