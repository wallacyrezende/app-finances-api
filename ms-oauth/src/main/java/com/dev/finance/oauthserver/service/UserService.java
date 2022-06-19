package com.dev.finance.oauthserver.service;

import com.dev.finance.oauthserver.entities.User;
import com.dev.finance.oauthserver.feignclients.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserFeignClient userFeignClient;
    public UserService(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("looking for user with email : " +email);

        User user = userFeignClient.findByEmail(email).getBody();

        if(Optional.ofNullable(user).isEmpty())
            throw new UsernameNotFoundException(email + "not found");

        return user;
    }
}
