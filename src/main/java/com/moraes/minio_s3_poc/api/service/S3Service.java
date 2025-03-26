package com.moraes.minio_s3_poc.api.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moraes.minio_s3_poc.api.service.interfaces.IS3Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Object;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3Service implements IS3Service {

    public static final String FILENAME = "filename";

    private final Environment environment;

    private S3Client s3Client;

    private String bucketName;

    @PostConstruct
    private void postConstruct() {
        bucketName = environment.getProperty("moraes.s3.bucketName");
        createClientConnection();
    }

    @SneakyThrows
    @Override
    public String uploadObject(MultipartFile file) {
        Path tempFile = null;
        try {
            final String key = String.valueOf(new Date().getTime()).concat("_").concat(UUID.randomUUID().toString());
            Map<String, String> metadata = new HashMap<>();
            metadata.put("company", "Baeldung");
            metadata.put("environment", "development");
            metadata.put("date", key);
            metadata.put(FILENAME, file.getOriginalFilename());

            tempFile = Files.createTempFile("upload_", file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            s3Client.putObject(request -> request
                    .bucket(bucketName)
                    .key(key)
                    .metadata(metadata)
                    .ifNoneMatch("*"),
                    tempFile);
            deleteIfExists(tempFile);
            return key;
        } catch (Exception e) {
            log.error("uploadObject error {}", e.getMessage(), e);
            deleteIfExists(tempFile);
            throw e;
        }
    }

    @Override
    public void delete(String objectKey) {
        s3Client.deleteObject(request -> request
                .bucket(bucketName)
                .key(objectKey));
    }

    @Override
    public Map<String, Object> getBykey(String key) {
        try {
            final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            final ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(getObjectRequest);
            final Resource resource = new ByteArrayResource(s3Object.asByteArray());
            return Map.of("resource", resource, FILENAME, s3Object.response().metadata().get(FILENAME));
        } catch (Exception e) {
            log.error("getBykey error {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<S3Object> getAll() {
        final ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        final ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        return listObjectsV2Response.contents();
    }

    private void createClientConnection() {
        final String accessKey = environment.getProperty("moraes.s3.accessKey");
        final String secretKey = environment.getProperty("moraes.s3.secretKey");
        final String host = environment.getProperty("moraes.s3.host");
        final AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3Client = S3Client
                .builder()
                .endpointOverride(URI.create(host))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(Boolean.TRUE)
                .build();

        if (!bucketExists(bucketName)) {
            createBucket();
        }
    }

    private void createBucket() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception exception) {
            log.error("createBucket {}", exception.getMessage(), exception);
        }
    }

    private boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(request -> request.bucket(bucketName));
            return true;
        } catch (NoSuchBucketException exception) {
            log.error("bucketExists {}", exception.getMessage());
        } catch (Exception exception) {
            log.error("bucketExists | error cause {} | {}", exception.getCause(), exception.getMessage(), exception);
        }
        return false;
    }

    private void deleteIfExists(Path tempFile) throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }
}