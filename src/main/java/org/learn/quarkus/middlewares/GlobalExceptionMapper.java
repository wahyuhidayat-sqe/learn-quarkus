package org.learn.quarkus.middlewares;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.learn.quarkus.schema.ErrorResponse;

/**
 * A global exception mapper for handling uncaught exceptions in a RESTful application.
 * <p>
 * This mapper implements the {@link ExceptionMapper} interface to catch exceptions
 * of type {@link Exception} that are not explicitly handled by other mappers.
 * <p>
 * When an uncaught exception is detected, it generates an HTTP response with the
 * following characteristics:
 * - The status code is set to 500 (INTERNAL_SERVER_ERROR).
 * - The response body comprises an instance of {@link ErrorResponse} containing an error message
 * based on the exception's message.
 * <p>
 * This global exception mapper ensures a uniform and consistent error response structure
 * for unhandled exceptions, improving the robustness and maintainability of the application.
 * <p>
 * Note: Specific exception types can be mapped separately by creating other
 * implementations of {@link ExceptionMapper}.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.builder().error(exception.getMessage()).build())
                .build();
    }
}

