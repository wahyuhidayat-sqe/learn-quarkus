package org.learn.quarkus.schema;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@RegisterForReflection
public class ApiResponse<T> {
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .message("Success")
                .data(data)
                .build();
    }
}
