package com.dev.finances.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("datasource")
@NoArgsConstructor
@Getter
@Setter
public class RemoteSecurityConfig {

    private String url;
    private String username;
    private String password;
    private String driverClassName;

}