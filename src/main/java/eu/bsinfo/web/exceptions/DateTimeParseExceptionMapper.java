package eu.bsinfo.web.exceptions;

import eu.bsinfo.web.dto.ErrorDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.time.format.DateTimeParseException;

public class DateTimeParseExceptionMapper implements ExceptionMapper<DateTimeParseException> {
    @Override
    public Response toResponse(DateTimeParseException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDto(e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
