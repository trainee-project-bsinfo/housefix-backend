package eu.bsinfo.web.api;

import com.google.common.annotations.VisibleForTesting;
import eu.bsinfo.Main;
import eu.bsinfo.db.ObjectMapper;
import eu.bsinfo.db.PreparedStatementBuilder;
import eu.bsinfo.db.SQLStatement;
import eu.bsinfo.db.enums.KindOfMeter;
import eu.bsinfo.db.enums.Tables;
import eu.bsinfo.utils.UUIDUtils;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.ErrorDto;
import eu.bsinfo.db.models.Reading;
import eu.bsinfo.web.dto.ReadingDto;
import eu.bsinfo.web.dto.ReadingsDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/readings")
public class ReadingsController {
    private SQLStatement stmt = new SQLStatement(Server.getDbConn());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings(@QueryParam("customer") UUID customerId,
                                @QueryParam("start") String start,
                                @QueryParam("end") String end,
                                @QueryParam("kindOfMeter") KindOfMeter kindOfMeter) {
        try {
            // NOTE: the following code is simpler but less performant
            /*List<Reading> readings = stmt.getReadings();

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
            }*/
            List<Object> sqlParams = new ArrayList<>();
            PreparedStatementBuilder stmtBuilder = new PreparedStatementBuilder()
                    .select(new String[]{"*"})
                    .from(Tables.READINGS);

            if (customerId != null) {
                stmtBuilder.where("customer_id", "=");
                sqlParams.add(customerId);
            }

            if (start != null && end != null) {
                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = LocalDate.parse(end);

                if (customerId != null) {
                    stmtBuilder.and("dateOfReading", ">=");
                    stmtBuilder.and("dateOfReading", "<=");
                } else {
                    stmtBuilder.where("dateOfReading", ">=");
                    stmtBuilder.and("dateOfReading", "<=");
                }

                sqlParams.add(startDate);
                sqlParams.add(endDate);
            } else if (start != null) {
                LocalDate startDate = LocalDate.parse(start);

                if (customerId != null) {
                    stmtBuilder.and("dateOfReading", ">=");
                } else {
                    stmtBuilder.where("dateOfReading", ">=");
                }

                sqlParams.add(startDate);
            } else if (end != null) {
                LocalDate endDate = LocalDate.parse(end);

                if (customerId != null) {
                    stmtBuilder.and("dateOfReading", "<=");
                } else {
                    stmtBuilder.where("dateOfReading", "<=");
                }

                sqlParams.add(endDate);
            }

            if (kindOfMeter != null) {
                if (stmtBuilder.getSql().contains("WHERE")) {
                    stmtBuilder.and("kindOfMeter", "=");
                } else {
                    stmtBuilder.where("kindOfMeter", "=");
                }
                sqlParams.add(kindOfMeter);
            }

            PreparedStatement prepStmt = stmtBuilder.build(Server.getDbConn());
            for (int i = 0; i < sqlParams.size(); i++) {
                Object param = sqlParams.get(i);
                if (param instanceof UUID) {
                    prepStmt.setBytes(i + 1, UUIDUtils.UUIDAsBytes((UUID)param));
                } else if (param instanceof LocalDate) {
                    prepStmt.setDate(i + 1, Date.valueOf((LocalDate)param));
                } else if (param instanceof Enum<?>) {
                    prepStmt.setString(i + 1, param.toString());
                }
            }

            List<Reading> readings = ObjectMapper.getReadings(prepStmt.executeQuery(), stmt);

            return Response.ok(new ReadingsDto(readings)).build();
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
            if (reading.getCustomer() == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDto("Customer is missing"))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            if (stmt.getCustomer(reading.getCustomer().getid()) == null) {
                stmt.createCustomer(reading.getCustomer());
            }

            stmt.createReading(reading);

            return Response
                    .ok(new ReadingDto(reading))
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
            if (reading.getCustomer() == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorDto("Customer is missing"))
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
            return Response.ok(new ReadingDto(reading)).build();
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
            return Response.ok(new ReadingDto(reading)).build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @VisibleForTesting
    public void setStmt(SQLStatement stmt) {
        if (!Main.isInTestMode()) {
            throw Main.getOnlyForTestingException();
        }
        this.stmt = stmt;
    }
}
