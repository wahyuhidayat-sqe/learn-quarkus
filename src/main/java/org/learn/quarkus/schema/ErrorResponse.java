package org.learn.quarkus.schema;

import lombok.Builder;

import java.util.List;

@Builder
public class ErrorResponse {
    public String error;
    public List<ErrorDetails> details;
}


