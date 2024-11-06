package eu.bsinfo.web.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
public class HealthController {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHealth() {
        return "OK";
    }
}
