package com.sebastian_daschner.jsr_370_examples.nio;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NioClient {

    public void upload() {
        final WebTarget target = ClientBuilder.newClient().target("");
        final ByteArrayInputStream in = new ByteArrayInputStream("hello".getBytes());
        final byte[] buffer = new byte[1000];

        target.request(MediaType.APPLICATION_OCTET_STREAM).nio().post(
                out -> {
                    try {
                        final int length = in.read(buffer);
                        if (length >= 0) {
                            out.write(buffer, 0, length);
                            return true;
                        }
                        in.close();
                        return false;
                    } catch (IOException e) {
                        throw new WebApplicationException(e);
                    }
                });
    }

    public void download() {
        final WebTarget target = ClientBuilder.newClient().target("");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1000];

        target.request().accept(MediaType.APPLICATION_OCTET_STREAM).nio().get(
                in -> {
                    try {
                        if (in.isFinished()) {
                            out.close();
                            // processing the output further ...
                        } else {
                            final int length = in.read(buffer);
                            out.write(buffer, 0, length);
                        }
                    } catch (IOException e) {
                        throw new WebApplicationException(e);
                    }
                });
    }

}
