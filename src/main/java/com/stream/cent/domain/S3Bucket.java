package com.stream.cent.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "s3_buckets")
@Getter
@Setter
@NoArgsConstructor
public class S3Bucket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bucket_name", nullable = false, unique = true)
    private String bucketName;

    @Column(name = "region")
    private String region;


}
