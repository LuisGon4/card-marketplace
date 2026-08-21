package com.spring.cardmarketplace.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties(prefix = "aws.s3")
public record DevS3Properties (
        String endpoint,
        String accessKey,
        String secretKey
){}
