package com.stream.cent.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "s3_metadata")
@Getter
@Setter
@NoArgsConstructor
public class S3Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bucket_id", referencedColumnName = "id")
    private S3Bucket s3Bucket;

    @Column(name = "object_key", nullable = false)
    private String objectKey;
}
