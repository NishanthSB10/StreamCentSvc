package com.stream.cent.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    @Value("${security.jwt.secret.key}")
    private String secretKey;

    @Value("${security.jwt.expiration.time}")
    private long jwtExpiration;

    @Value("${security.jwt.cookie.name}")
    private String jwtCookieName;

    @Value("${security.refresh.token.expiration.time}")
    private long refreshTokenExpirationTime;

    @Value("${security.refresh.token.cookie.name}")
    private String refreshTokenCookieName;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.s3.bucketName}")
    private String awsS3BucketName;

    @Value("${aws.s3.region}")
    private String awsRegion;


}
