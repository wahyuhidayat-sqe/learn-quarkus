package org.learn.quarkus.middlewares;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.learn.quarkus.schema.ErrorDetails;
import org.learn.quarkus.schema.ErrorResponse;

/**
 * Maps {@link ConstraintViolationException} to an HTTP Response.
 * <p>
 * This class implements {@link ExceptionMapper} to provide custom handling
 * for exceptions of type {@link ConstraintViolationException}. When a
 * validation failure occurs, this mapper constructs a structured response
 * containing error details and a message indicating the validation failure.
 * <p>
 * The response returned has a status code of 400 (BAD_REQUEST) to indicate
 * a client-side error. Each constraint violation is transformed into an
 * instance of {@link ErrorDetails} containing
 * the specific validation message and the associated field/property that
 * caused the violation.
 * <p>
 * The response structure includes:
 * - An error message "Validation failed"
 * - A list of error details, where each entry describes a specific constraint violation
 * <p>
 * This mapper ensures consistent error handling and structured responses
 * for validation-related errors in the application.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final String errorMessage = "Validation failed";

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var violations = exception.getConstraintViolations();
        var details = violations.stream()
                .map(violation -> ErrorDetails
                        .builder()
                        .message(violation.getMessage())
                        .field(violation.getPropertyPath().toString())
                        .build())
                .toList();

        var response = ErrorResponse.builder()
                .error(errorMessage)
                .details(details)
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}
