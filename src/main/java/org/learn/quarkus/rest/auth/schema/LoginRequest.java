package org.learn.quarkus.rest.auth.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
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
