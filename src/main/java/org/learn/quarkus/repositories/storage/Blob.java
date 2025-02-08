package org.learn.quarkus.repositories.storage;

import lombok.Builder;

@Builder
public class Blob {
    public String name;
    public String path;
    public String contentType;
    public long size;
}
