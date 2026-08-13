package com.spring.cardmarketplace.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@EnableConfigurationProperties({S3Properties.class, DevS3Properties.class})
@Configuration
public class S3Config {
    @Profile("dev")
    @Bean
    public S3Client devS3Client(S3Properties props, DevS3Properties devProps) {
        return S3Client.builder()
                .region(Region.of(props.region()))
                .endpointOverride(URI.create(devProps.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(devProps.accessKey(), devProps.secretKey())))
                .forcePathStyle(true)
                .build();
    }

    @Profile("!dev")
    @Bean
    public S3Client s3Client(S3Properties props) {
        return S3Client.builder()
                .region(Region.of(props.region()))
                .build();
    }


    @Profile("!dev")
    @Bean
    public S3Presigner s3Presigner(S3Properties props) {
        return S3Presigner.builder()
                .region(Region.of(props.region()))
                .build();
    }

    @Profile("dev")
    @Bean
    public S3Presigner devS3Presigner(S3Properties props, DevS3Properties devProps) {
        return S3Presigner.builder()
                .region(Region.of(props.region()))
                .endpointOverride(URI.create(devProps.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(devProps.accessKey(), devProps.secretKey())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
