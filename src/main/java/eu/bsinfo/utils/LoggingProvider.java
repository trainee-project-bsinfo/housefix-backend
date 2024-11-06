package eu.bsinfo.utils;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class LoggingProvider implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(LoggingProvider.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOGGER.log(Level.INFO, "Request: {0} {1}", new Object[]{
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()
        });
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        LOGGER.log(Level.INFO, "Response: {0} - Status: {1}", new Object[]{
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus()
        });
    }
}
