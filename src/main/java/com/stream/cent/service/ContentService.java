package com.stream.cent.service;

import com.stream.cent.domain.User;
import com.stream.cent.domain.VideoMetadata;
import com.stream.cent.enums.VideoMetadataStatusEnum;
import com.stream.cent.repository.VideoMetadataRepository;
import com.stream.cent.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

@Service
public class ContentService {

    @Autowired
    private FileEventProducer fileEventProducer;
    @Autowired
    private UserService userService;
    @Autowired
    private VideoMetadataRepository videoMetadataRepository;

    public String uploadVideo(MultipartFile file) throws IOException {
        File tempDir = new File("C:/Users/nsb/content-storage/temp/videos"); //store the file temporarily
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File convFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(convFile);

        VideoMetadata videoMetadata = saveVideoMetadata(file,convFile.getPath());
        fileEventProducer.sendVideoUploadedEvent(videoMetadata);

        return file.getOriginalFilename();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        File tempDir = new File("C:/Users/nsb/content-storage/temp/files");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File convFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(convFile);

//        fileEventProducer.sendFileUploadedEvent(convFile.getPath());

        return file.getOriginalFilename();
    }

    public VideoMetadata saveVideoMetadata(MultipartFile file, String path){
        String username = SecurityUtil.getCurrentUsername();
        User user = userService.findByUsername(username);

        VideoMetadata videoMetadata = new VideoMetadata();
        videoMetadata.setContentType(file.getContentType());
        videoMetadata.setFileName(file.getOriginalFilename());
        videoMetadata.setFileSize(file.getSize());
        videoMetadata.setUploadedBy(user);
        videoMetadata.setUploadedOn(new Timestamp(System.currentTimeMillis()));
        videoMetadata.setTempPath(path);
        videoMetadata.setStatus(VideoMetadataStatusEnum.PROCESSING.getValue());

        return videoMetadataRepository.save(videoMetadata);
    }
}
