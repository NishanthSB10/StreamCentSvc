package com.stream.cent.service;

import com.stream.cent.domain.VideoMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FileEventProducer {


    @Autowired
    private KafkaTemplate<String, VideoMetadata> kafkaTemplate;


//    public void sendFileUploadedEvent(String fileKey) {
//        kafkaTemplate.send("file-events", fileKey);
//    }

    public void sendVideoUploadedEvent(VideoMetadata videoMetadata) {
        kafkaTemplate.send("video-events", videoMetadata);
    }
}
