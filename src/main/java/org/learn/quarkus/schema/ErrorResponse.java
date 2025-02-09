package org.learn.quarkus.schema;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;

import java.util.List;

@Builder
@RegisterForReflection
public class ErrorResponse {
    public String error;
    public List<ErrorDetails> details;
}


