package eu.bsinfo.web.exceptions;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import eu.bsinfo.web.dto.ErrorDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ValueInstantiationExceptionMapper implements ExceptionMapper<ValueInstantiationException> {
    @Override
    public Response toResponse(ValueInstantiationException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDto(e.getCause().getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
