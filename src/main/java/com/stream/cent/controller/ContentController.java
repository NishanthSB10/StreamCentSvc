package com.stream.cent.controller;

import com.stream.cent.service.ContentService;
import com.stream.cent.service.S3StorageService;
import com.stream.cent.utils.ApiResponse;
import com.stream.cent.utils.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class ContentController {

    private final S3StorageService s3StorageService;

    @Autowired
    public ContentController(S3StorageService s3StorageService) {
        this.s3StorageService = s3StorageService;
    }

    @Autowired
    private ContentService contentService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        Map<String, Object> dataMap =  new HashMap<>();

        ApiResponse apiResponse = new EntityStatus().success("content-storage","VIDEO_UPLOAD", dataMap,
                "File Upload In-Progress",
                "File Upload In-Progress");
        if (contentType != null && contentType.startsWith("video")) {
            return contentService.uploadVideo(file);
        } else {
            return contentService.uploadFile(file);
        }

    }

    @GetMapping("/metadata/{fileKey}")
    public String getFileMetadata(@PathVariable String fileKey) throws IOException {

        return s3StorageService.getFileMetadata(fileKey).toString();
    }

    @GetMapping("/download/{fileKey}")
    public byte[] downloadFile(@PathVariable String fileKey) {
        return s3StorageService.downloadFile(fileKey);
    }

    @GetMapping("/stream/{fileId}")
    public ResponseEntity<InputStreamResource> stream(
            @PathVariable String fileId,
            @RequestHeader("Range") String rangeHeader) throws IOException {

        // Parse range
        long rangeStart = Long.parseLong(rangeHeader.replace("bytes=", "").split("-")[0]);

        // Optional end support
        long rangeEnd = rangeStart + 1_000_000; // ~1MB

        ResponseInputStream<GetObjectResponse> s3Stream = s3StorageService.getObject(fileId, rangeStart, rangeEnd);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd)
                .body(new InputStreamResource(s3Stream));
    }
}

