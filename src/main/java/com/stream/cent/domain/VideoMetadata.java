package com.stream.cent.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "video_metadata")
@Getter
@Setter
@NoArgsConstructor
public class VideoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "status",length = 255, nullable = false)
    private String status;

    @Column(name = "file_size", length = 255)
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "temp_path")
    private String tempPath;

    @OneToOne
    @JoinColumn(name = "s3_metadata_id", referencedColumnName = "id")
    private S3Metadata s3Metadata;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploaded_by", referencedColumnName = "id")
    private User uploadedBy;

    @Column(name = "uploaded_on")
    private Timestamp uploadedOn;
}

