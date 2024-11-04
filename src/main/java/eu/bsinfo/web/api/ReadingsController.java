package eu.bsinfo.web.api;

import eu.bsinfo.db.SQLStatement;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.ErrorDto;
import eu.bsinfo.web.dto.Reading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Path("/readings")
public class ReadingsController {
    private final SQLStatement stmt = new SQLStatement(Server.getDbConn());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        try {
            List<Reading> readings = stmt.getReadings();
            return Response.ok(readings).build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReading(Reading reading) {
        try {
            stmt.createReading(reading);
            return Response.ok(reading).build();
        } catch (SQLException e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateReading(Reading reading) {
        try {
            int affectedRows = stmt.updateReading(reading);
            if (affectedRows != 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    @Path("/{readingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReading(@PathParam("readingId") UUID readingId) {
        try {
            Reading reading = stmt.getReading(readingId);
            if (reading == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(reading).build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/{readingId}")
    public Response deleteReading(@PathParam("readingId") UUID readingId) {
        try {
            int affectedRows = stmt.deleteReading(readingId);
            if (affectedRows != 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
