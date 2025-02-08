package org.learn.quarkus.repositories.storage;

import java.io.InputStream;

public interface StorageRepository {
    Blob uploadFile(InputStream inputStream, String fileName, String contentType);

    String getDownloadUrl(String filePath);
}
