package org.learn.quarkus.middlewares;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.security.auth.message.AuthException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.learn.quarkus.models.User;
import org.learn.quarkus.schema.ErrorResponse;
import org.learn.quarkus.services.AuthService;

import java.util.HashMap;

/**
 * A container request filter used for JWT-based authentication in a RESTful application.
 * <p>
 * This filter intercepts incoming HTTP requests and verifies whether the requests have a
 * valid JSON Web Token (JWT) for protected endpoints. If no valid token is found, the
 * request is aborted with an HTTP 401 (Unauthorized) response.
 * <p>
 * The filter checks for the following key features:
 * - Determines if the requested endpoint is public. Public endpoints do not require
 * authentication and are defined in the `PUBLIC_URLS` map.
 * <br>
 * - For non-public endpoints, it extracts the "Authorization" header, validates the JWT,
 * and fetches the corresponding user using the `AuthService`. The validated user
 * is stored in the request context for use during subsequent request handling.
 * <br>
 * - In case of missing or invalid tokens, the filter generates an appropriate error
 * response with an HTTP 401 status.
 * <p>
 * Priority:
 * <br>
 * This filter has a high priority of 1000, ensuring it executes before other request
 * processing logic.
 * <p>
 * Dependencies:
 * <br>
 * - Injects the {@link AuthService} to verify and fetch user details from the token.
 * <p>
 * Methods:
 * <br>
 * - {@code getUserFromContext}: A static utility method to retrieve the authenticated
 * user from the request context.
 * <p>
 */
@Provider
@Priority(1000) // High priority to execute before request processing
@Slf4j
public class JwtRequestFilter implements ContainerRequestFilter {


    private static final HashMap<String, String> PUBLIC_URLS = new HashMap<>() {
        {
            put("POST:/users", "Create a new user");
            put("POST:/auth/login", "Login to the application");
        }
    };

    static final String AUTHORIZATION_HEADER = "Authorization";
    static final String PROPERTY_NAME = "user";
    static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    static final String MISSING_TOKEN_MESSAGE = "Missing token";

    /**
     * Extracts the current authenticated user from the request context.
     * <p>
     * This method retrieves the user object stored as a property within the given
     * {@code ContainerRequestContext}. If the property does not exist or is not
     * an instance of {@link User}, an exception is thrown.
     *
     * @param requestContext The {@link ContainerRequestContext} containing the request data,
     *                       from which the user will be retrieved.
     * @return The authenticated {@link User} instance associated with the request.
     * @throws WebApplicationException If the user property is missing or invalid, with
     *                                 a 401 Unauthorized response status.
     */
    public static User getUserFromContext(ContainerRequestContext requestContext) {

        var user = requestContext.getProperty(PROPERTY_NAME);
        if (user instanceof User) {
            return (User) user;
        }
        throw new WebApplicationException(INVALID_TOKEN_MESSAGE, Response.Status.UNAUTHORIZED);
    }


    @Inject
    AuthService authService;

    private boolean isPublic(ContainerRequestContext requestContext) {
        var method = requestContext.getMethod();
        var path = requestContext.getUriInfo().getPath();
        var accessPath = method + ":" + path;
        return PUBLIC_URLS.containsKey(accessPath);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isPublic(requestContext)) {
            return;
        }
        String authHeader = requestContext.getHeaderString(AUTHORIZATION_HEADER);
        if (authHeader == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder().error(MISSING_TOKEN_MESSAGE).build())
                    .build());
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        try {
            var user = authService.verifyToken(token);
            requestContext.setProperty(PROPERTY_NAME, user);
        } catch (AuthException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder().error(e.getMessage()).build())
                    .build());
        }
    }
}
