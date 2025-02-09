package org.learn.quarkus.repositories.storage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class IS3StorageRepository implements StorageRepository {

    private static final String UPLOAD_PATH_PREFIX = "uploads/";

    private final S3Client client;
    private final String bucketName;
    private final S3Presigner presigner;

    @Inject
    public IS3StorageRepository(
            @ConfigProperty(name = "app.storage.bucket-name") String bucketName,
            @ConfigProperty(name = "app.storage.region") String region
    ) {
        this.bucketName = bucketName;
        this.client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public Blob uploadFile(InputStream inputStream, String fileName, String contentType) {
        var uniqueFileName = UUID.randomUUID() + fileName;
        var filePath = UPLOAD_PATH_PREFIX + uniqueFileName;
        var request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .contentType(contentType)
                .build();

        try {
            var size = inputStream.available();
            client.putObject(request, RequestBody.fromInputStream(inputStream, size));
            return Blob.builder()
                    .name(uniqueFileName)
                    .path(filePath)
                    .contentType(contentType)
                    .size(size)
                    .build();

        } catch (Exception e) {
            log.error("error when uploading to bucket : {}{}", bucketName, e.getMessage());
            return null;
        }
    }

    @Override
    public String getDownloadUrl(String filePath) {
        var request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();
        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(1))
                .getObjectRequest(request)
                .build();

        var signedUrl = presigner.presignGetObject(presignRequest);
        return signedUrl.url().toString();
    }
}
