package com.stream.cent.repository;


import com.stream.cent.domain.S3Metadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3MetadataRepository extends JpaRepository<S3Metadata, Long> {


}
