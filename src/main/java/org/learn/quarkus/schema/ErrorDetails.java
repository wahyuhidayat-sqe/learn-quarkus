package org.learn.quarkus.schema;

import lombok.Builder;

@Builder
public class ErrorDetails {
    public String field;
    public String message;

}
