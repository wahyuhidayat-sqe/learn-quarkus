package org.learn.quarkus.rest.user;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.learn.quarkus.configs.Configs;
import org.learn.quarkus.exception.DuplicateValueException;
import org.learn.quarkus.middlewares.JwtRequestFilter;
import org.learn.quarkus.models.User;
import org.learn.quarkus.rest.user.schema.CreateUserRequest;
import org.learn.quarkus.schema.ApiResponse;
import org.learn.quarkus.services.UserService;

import java.io.InputStream;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public ApiResponse<User> createUser(@Valid CreateUserRequest request) {
        try {
            var user = userService.createUser(request);
            return ApiResponse.ok(user);
        } catch (DuplicateValueException exception) {
            throw new WebApplicationException(exception.getMessage(), Status.BAD_REQUEST);
        }
    }

    @GET
    @SecurityRequirements(value = @SecurityRequirement(name = Configs.SECURITY_DEFINITION_NAME))
    public ApiResponse<List<User>> getUserList(@QueryParam("q") String q) {
        var users = userService.getUser(q);
        return ApiResponse.ok(users);
    }


    @GET
    @Path("/me")
    @SecurityRequirement(name = Configs.SECURITY_DEFINITION_NAME)
    public ApiResponse<User> me(
            @Context ContainerRequestContext context
    ) {
        var user = JwtRequestFilter.getUserFromContext(context);
        var url = userService.getPhotoUrl(user);
        user.setPhoto(url);
        return ApiResponse.ok(user);
    }


    @PUT
    @Path("/photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = Configs.SECURITY_DEFINITION_NAME)
    public ApiResponse<Boolean> changePhoto(
            @Context ContainerRequestContext context,
            @FormParam("file") InputStream file
    ) {
        var user = JwtRequestFilter.getUserFromContext(context);
        userService.uploadPhoto(user.id, file);
        return ApiResponse.ok(true);
    }
}
