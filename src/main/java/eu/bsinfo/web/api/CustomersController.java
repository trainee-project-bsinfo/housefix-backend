package eu.bsinfo.web.api;

import com.google.common.annotations.VisibleForTesting;
import eu.bsinfo.Main;
import eu.bsinfo.db.SQLStatement;
import eu.bsinfo.db.models.Customer;
import eu.bsinfo.db.models.Reading;
import eu.bsinfo.web.Server;
import eu.bsinfo.web.dto.CustomerDto;
import eu.bsinfo.web.dto.CustomerWithReadingsDto;
import eu.bsinfo.web.dto.CustomersDto;
import eu.bsinfo.web.dto.ErrorDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/customers")
public class CustomersController {
    private SQLStatement stmt = new SQLStatement(Server.getDbConn());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers() {
        try {
            List<Customer> customers = stmt.getCustomers();
            return Response.ok(new CustomersDto(customers)).build();
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
    public Response createCustomer(Customer customer) {
        try {
            stmt.createCustomer(customer);
            return Response.ok(new CustomerDto(customer))
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
    public Response updateCustomer(Customer customer) {
        try {
            int affectedRows = stmt.updateCustomer(customer);
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
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("customerId") UUID customerId) {
        try {
            Customer customer = stmt.getCustomer(customerId);
            if (customer == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(new CustomerDto(customer)).build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorDto(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("customerId") UUID customerId) {
        try {
            Customer customer = stmt.getCustomer(customerId);
            List<Reading> readings = stmt.getReadingsByCustomerId(customerId);

            int affectedRows = stmt.deleteCustomer(customerId);
            if (affectedRows != 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(new CustomerWithReadingsDto(customer, readings)).build();
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
        Main.ensureOnlyForTesting();
        this.stmt = stmt;
    }
}
