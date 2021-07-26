package com.cztx.gateway.config;

import com.cztx.gateway.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/19 10:26
 * @description:
 */
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFaillHandler authenticationFaillHandler;
    @Autowired
    private CustomHttpBasicServerAuthenticationEntryPoint customHttpBasicServerAuthenticationEntryPoint;
    @Autowired
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;

    //security的鉴权排除列表
    private static final String[] excludedAuthPages = {
            "/auth/login",
            "/auth/loginout",
            "/home/**",
            "/user/**",
            "/category/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(excludedAuthPages).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form ->{
                    form.loginPage("/auth/login")
                            .authenticationSuccessHandler(authenticationSuccessHandler) //认证成功
                            .authenticationFailureHandler(authenticationFaillHandler) //登陆验证失败
                            .and().exceptionHandling().authenticationEntryPoint(customHttpBasicServerAuthenticationEntryPoint)  //基于http的接口请求鉴权失败
                            ;
                })
                .logout(logoutSpec -> {
                    logoutSpec
                            .logoutUrl("/auth/loginout")
                            .logoutSuccessHandler(authLogoutSuccessHandler);
                })
        .csrf().disable()
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return  PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
