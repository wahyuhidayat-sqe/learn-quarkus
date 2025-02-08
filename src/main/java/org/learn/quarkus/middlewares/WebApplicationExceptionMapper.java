package org.learn.quarkus.middlewares;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.learn.quarkus.schema.ErrorResponse;

/**
 * Handles exceptions of type {@link WebApplicationException}.
 * <p>
 * This class implements {@link ExceptionMapper} for processing exceptions
 * that are instances of {@link WebApplicationException}. It intercepts
 * the exception and creates an appropriate HTTP response with a structured
 * error payload.
 * <p>
 * The HTTP status code of the response is derived directly from the status
 * code of the {@link WebApplicationException} instance. The response body
 * contains an instance of {@link ErrorResponse}, which includes an error
 * message based on the exception message.
 * <p>
 * This mapper ensures consistent error handling and structured error responses
 * for applications using exceptions of type {@link WebApplicationException}.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException e) {
        var statusCode = e.getResponse().getStatus();
        var errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
        return Response.status(statusCode)
                .entity(errorResponse)
                .build();
    }
}
