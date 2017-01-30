package com.sebastian_daschner.jsr_370_examples.nio;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Path("data")
public class DataResource {

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download() {
        final InputStream in = new ByteArrayInputStream("hello".getBytes());
        final byte[] buffer = new byte[1000];

        return Response.ok().entity(
                out -> {
                    try {
                        final int n = in.read(buffer);
                        if (n >= 0) {
                            out.write(buffer, 0, n);
                            return true;
                        }
                        in.close();
                        return false;
                    } catch (IOException e) {
                        throw new WebApplicationException(e);
                    }
                }).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void upload(@QueryParam("path") String path, @Context Request request) {

        // ...
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1000];

        request.entity(
                in -> {
                    try {
                        if (in.isFinished()) {
                            out.close();
                        } else {
                            final int n = in.read(buffer);
                            out.write(buffer, 0, n);
                        }
                    } catch (IOException e) {
                        throw new WebApplicationException(e);
                    }
                });
    }

}

