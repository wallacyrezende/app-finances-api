package com.dev.finance.apigateway.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("security-config")
@NoArgsConstructor
@Getter
@Setter
public class RemoteSecurityConfig {

    private String tokenSigningKey;

}