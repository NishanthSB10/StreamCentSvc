package com.stream.cent.service;
import com.stream.cent.domain.VideoMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FileEventConsumer {

    @Autowired
    private VideoService videoService;

    @KafkaListener(topics = "file-events", groupId = "file-processing-group")
    public void processFileEvent(String fileKey) throws InterruptedException {
        System.out.println("Processing file: " + fileKey);
        Thread.sleep(15000);
        System.out.println("Processed file: " + fileKey);
    }

    @KafkaListener(topics = "video-events", groupId = "file-processing-group")
    public void processVideoEvent(VideoMetadata videoMetadata) throws Exception {
        System.out.println("Processing video: " + videoMetadata.getFileName());
        videoService.processVideo(videoMetadata);
    }
}
