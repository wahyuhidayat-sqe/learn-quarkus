package org.learn.quarkus.rest.auth.schema;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import org.learn.quarkus.models.primary.User;

@Builder
@RegisterForReflection
public class LoginData {
    public User user;
    public String token;
}
