package eu.bsinfo.web.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import eu.bsinfo.web.dto.ErrorDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {
    @Override
    public Response toResponse(InvalidFormatException exception) {
        String message = String.format("Invalid value for field '%s': %s. Expected type: %s.",
                exception.getPath().get(0).getFieldName(),
                exception.getValue(),
                exception.getTargetType().getSimpleName());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDto(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
