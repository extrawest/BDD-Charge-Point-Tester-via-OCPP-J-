package com.extrawest.jsonserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@Getter
public class ApplicationConfiguration {

    @Value("${host.address:}")
    @Setter private String hostAddress;

    @Value("${server.port}")
    private Integer serverPort;

}
