package com.dev.finance.authserver.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final RemoteSecurityConfig remoteSecurityConfig;
    private final JwtAccessTokenConverter tokenConverter;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenStore tokenStore;
    private final AuthenticationManager authenticationManager;

    public AuthorizationServerConfig(@Qualifier("accessTokenConverter") JwtAccessTokenConverter tokenConverter, BCryptPasswordEncoder passwordEncoder, JwtTokenStore tokenStore, AuthenticationManager authenticationManager, RemoteSecurityConfig remoteSecurityConfig) {
        this.tokenConverter = tokenConverter;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
        this.authenticationManager = authenticationManager;
        this.remoteSecurityConfig = remoteSecurityConfig;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        security.tokenKeyAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(remoteSecurityConfig.getClientId())
                .secret(passwordEncoder.encode(remoteSecurityConfig.getClientSecret()))
                .scopes("read", "write")
                .authorizedGrantTypes("password")
                .accessTokenValiditySeconds(remoteSecurityConfig.getTokenExpiration());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore)
                .accessTokenConverter(tokenConverter);
    }

}
