package com.moraes.minio_s3_poc.api.service.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.model.S3Object;

public interface IS3Service {
    
    String uploadObject(MultipartFile file);

    void delete(String objectKey);

    List<S3Object> getAll();

    Map<String, Object> getBykey(String key);
}
