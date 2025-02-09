package org.learn.quarkus.schema;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

@Builder
@RegisterForReflection
public class ErrorDetails {
    public String field;
    public String message;
}
