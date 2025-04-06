package com.stream.cent.repository;


import com.stream.cent.domain.S3Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3BucketRepository extends JpaRepository<S3Bucket, Long> {

    S3Bucket findByBucketName(String bucketName);

}
