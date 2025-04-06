package com.stream.cent.service;

import com.stream.cent.config.AppConfig;
import com.stream.cent.domain.S3Bucket;
import com.stream.cent.domain.S3Metadata;
import com.stream.cent.domain.VideoMetadata;
import com.stream.cent.enums.VideoMetadataStatusEnum;
import com.stream.cent.repository.S3BucketRepository;
import com.stream.cent.repository.S3MetadataRepository;
import com.stream.cent.repository.VideoMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
    @Autowired
    private S3StorageService s3StorageService;
    @Autowired
    private S3BucketRepository s3BucketRepository;
    @Autowired
    private S3MetadataRepository s3MetadataRepository;
    @Autowired
    private VideoMetadataRepository videoMetadataRepository;
    @Autowired
    private AppConfig appConfig;

    public void processVideo(VideoMetadata videoMetadata) throws Exception {
        try {
            String tempPath = videoMetadata.getTempPath();
            Path videoPath = Paths.get(tempPath);
            S3Bucket s3Bucket = s3BucketRepository.findByBucketName(appConfig.getAwsS3BucketName());

            if (!Files.exists(videoPath)) {
                throw new FileNotFoundException("File not found at: " + tempPath);
            }

            String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe"; // Update if ffmpeg is in PATH

            Path tempDir = Files.createTempDirectory("hls-out");
            Path hlsOutputDir = tempDir.resolve(UUID.randomUUID().toString());
            Files.createDirectories(hlsOutputDir);

            String baseOutput = hlsOutputDir.toAbsolutePath().toString();

            // 1. FFmpeg command with multiple resolutions
            List<String> command = List.of(
                    ffmpegPath,
                    "-i", videoPath.toString(),

                    "-filter_complex",
                    "[0:v]split=3[v1][v2][v3];" +
                            "[v1]scale=w=1920:h=1080[vout1];" +
                            "[v2]scale=w=1280:h=720[vout2];" +
                            "[v3]scale=w=854:h=480[vout3]",

                    "-map", "[vout1]", "-c:v:0", "libx264", "-b:v:0", "5000k",
                    "-map", "[vout2]", "-c:v:1", "libx264", "-b:v:1", "3000k",
                    "-map", "[vout3]", "-c:v:2", "libx264", "-b:v:2", "1500k",

                    "-f", "hls",
                    "-hls_time", "6",
                    "-hls_list_size", "0",
                    "-hls_segment_filename", baseOutput + "/v%v_%03d.ts",
                    "-master_pl_name", "master.m3u8",
                    "-var_stream_map", "v:0,name:1080p v:1,name:720p v:2,name:480p",

                    baseOutput + "/v%v.m3u8"
            );

            // 2. Run FFmpeg
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFMPEG] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg processing failed with exit code: " + exitCode);
            }

            // 3. Upload all HLS files to S3
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(hlsOutputDir)) {
                for (Path filePath : stream) {
                    String s3Key = "videos/" + hlsOutputDir.getFileName() + "/" + filePath.getFileName();
                    s3StorageService.uploadVideo(s3Key, s3Bucket.getBucketName(), filePath);
                }
            }

            S3Metadata s3Metadata = new S3Metadata();
            s3Metadata.setObjectKey(hlsOutputDir.getFileName().toString());
            s3Metadata.setS3Bucket(s3Bucket);

            s3Metadata = s3MetadataRepository.save(s3Metadata);

            videoMetadata.setS3Metadata(s3Metadata);
            videoMetadata.setStatus(VideoMetadataStatusEnum.SUCCESS.getValue());

            videoMetadataRepository.save(videoMetadata);

            Files.deleteIfExists(videoPath);

            System.out.println(s3StorageService.getPublicUrl("videos/" + hlsOutputDir.getFileName() + "/master.m3u8"));
        }
        catch (Exception e){
            videoMetadata.setStatus(VideoMetadataStatusEnum.FAILED.getValue());
            videoMetadataRepository.save(videoMetadata);
        }


    }

}
