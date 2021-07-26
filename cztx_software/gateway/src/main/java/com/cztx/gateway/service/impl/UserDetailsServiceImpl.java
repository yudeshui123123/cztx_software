package com.cztx.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/19 10:45
 * @description:
 */
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        if(username.equals("xiaoming")){
            UserDetails admin = User.withUsername(username).password(passwordEncoder.encode("123")).authorities("admin").build();
            return Mono.just(admin);
        }
        return Mono.error(new UsernameNotFoundException("用户名不存在"));
    }
}
