package org.learn.quarkus.rest.auth.schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class LoginRequest {
    @Email
    @Size(max = 100)
    @Schema(description = "email", examples = "john@mail.com")
    public String email;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "password", examples = "Secure@123")
    public String password;
}
