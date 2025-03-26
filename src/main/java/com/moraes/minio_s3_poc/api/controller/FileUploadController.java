package com.moraes.minio_s3_poc.api.controller;

import static com.moraes.minio_s3_poc.api.service.S3Service.FILENAME;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moraes.minio_s3_poc.api.service.interfaces.IS3Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.S3Object;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final IS3Service service;

    @GetMapping("/{key}")
    public ResponseEntity<Resource> getBykey(@PathVariable String key) {
        Map<String, Object> response = service.getBykey(key);
        final Resource resource = (Resource) response.get("resource");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=\"%s\"", response.get(FILENAME)))
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<String>> getAll() {
        final List<S3Object> list = service.getAll();
        return ResponseEntity.ok(list.stream().map(S3Object::key).toList());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        final String key = service.uploadObject(file);
        return ResponseEntity.created(URI.create(String.format("/files/%s", key))).body(key);
    }

    @DeleteMapping(value = "/{key}")
    public ResponseEntity<String> delete(@PathVariable String key) {
        service.delete(key);
        return ResponseEntity.noContent().build();
    }
}
