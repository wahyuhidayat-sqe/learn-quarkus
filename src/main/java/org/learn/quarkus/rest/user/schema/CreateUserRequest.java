package org.learn.quarkus.rest.user.schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class CreateUserRequest {

    @Size(min = 3, max = 20)
    @Schema(description = "User name", examples = "John")
    public String name;

    @Email()
    @NotBlank
    @Schema(description = "Email", examples = "john@mail.com")
    public String email;

    @Schema(
            description = "User's password, must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
            examples = "Secure@123",
            minLength = 8,
            maxLength = 100
    )
    @NotBlank
    @Size(min = 8, max = 100)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$",
            message = "Password must be at least 8 characters long, contain one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    public String password;
}
