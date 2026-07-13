package com.spring.cardmarketplace.dto.s3properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties (
        String endpoint,
        String region,
        String accessKey,
        String secretKey,
        String bucketName
){}
