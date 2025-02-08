package org.learn.quarkus.rest.auth.schema;

import lombok.Builder;
import org.learn.quarkus.models.User;

@Builder
public class LoginData {
    public User user;
    public String token;
}
