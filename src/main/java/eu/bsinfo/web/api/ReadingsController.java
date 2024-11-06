package eu.bsinfo.web.api;

import eu.bsinfo.db.SQLStatement;
import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.ErrorDto;
import eu.bsinfo.web.dto.Reading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Path("/readings")
public class ReadingsController {
    private final SQLStatement stmt = new SQLStatement(Server.getDbConn());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings(@QueryParam("customer") UUID customerId,
                                @QueryParam("start") String start,
                                @QueryParam("end") String end,
                                @QueryParam("kindOfMeter") KindOfMeter kindOfMeter) {
        try {
            List<Reading> readings = stmt.getReadings();

            if (customerId != null) {
                readings = stmt.getReadingsByCustomerId(customerId);
            }

            if (start != null && end != null) {
                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = LocalDate.parse(end);

                readings = readings.stream().filter(r ->
                        r.getDateOfReading().isAfter(startDate) &&
                        r.getDateOfReading().isBefore(endDate)
                ).toList();
            } else if (start != null) {
                LocalDate startDate = LocalDate.parse(start);
                readings = readings.stream().filter(r -> r.getDateOfReading().isAfter(startDate)).toList();
            } else if (end != null) {
                LocalDate endDate = LocalDate.parse(end);
                readings = readings.stream().filter(r -> r.getDateOfReading().isBefore(endDate)).toList();
            }

            if (kindOfMeter != null) {
                readings = readings.stream().filter(r -> r.getKindOfMeter().equals(kindOfMeter)).toList();
            }

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
            if (reading.getCustomer() == null || reading.getCustomer().getid() == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDto("Customer is missing or incomplete"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            if (stmt.getCustomer(reading.getCustomer().getid()) == null) {
                stmt.createCustomer(reading.getCustomer());
            }

            stmt.createReading(reading);

            return Response
                    .ok(reading)
                    .status(Response.Status.CREATED)
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateReading(Reading reading) {
        try {
            if (reading.getCustomer().getid() == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDto("Customer Id is missing"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            if (stmt.getCustomer(reading.getCustomer().getid()) == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(new ErrorDto("Customer not found"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            int affectedRows = stmt.updateReading(reading);
            if (affectedRows != 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok("OK").build();
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReading(@PathParam("readingId") UUID readingId) {
        try {
            Reading reading = stmt.getReading(readingId);
            int affectedRows = stmt.deleteReading(readingId);
            if (affectedRows != 1) {
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
}
