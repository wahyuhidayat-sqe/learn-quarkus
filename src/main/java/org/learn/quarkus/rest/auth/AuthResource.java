package org.learn.quarkus.rest.auth;

import jakarta.inject.Inject;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.learn.quarkus.configs.Configs;
import org.learn.quarkus.rest.auth.schema.LoginData;
import org.learn.quarkus.rest.auth.schema.LoginRequest;
import org.learn.quarkus.schema.ApiResponse;
import org.learn.quarkus.services.AuthService;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecuritySchemes(
        value = @SecurityScheme(
                securitySchemeName = Configs.SECURITY_DEFINITION_NAME,
                type = SecuritySchemeType.HTTP,
                scheme = "bearer"
        )
)
public class AuthResource {

    @Inject
    public AuthService authService;


    @POST
    @Path("/login")
    public ApiResponse<LoginData> login(@Valid LoginRequest request) {
        try {
            var data = authService.login(request.email, request.password);
            return ApiResponse.ok(data);
        } catch (AuthException exception) {
            throw new WebApplicationException(exception.getMessage(), Response.Status.UNAUTHORIZED);
        }
    }
}
