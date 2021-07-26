package com.cztx.gateway.handler;

import com.cztx.common.entity.CommonResult;
import com.cztx.common.myenum.MyEnum;
import com.cztx.common.util.JWTUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/24 14:31
 * @description:
 * 登录成功拦截器
 */
@Slf4j
@Component
public class AuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler{

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        //httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization");
        //设置body
        CommonResult<String> wsResponse = CommonResult.builder();
        byte[] dataBytes={};
        ObjectMapper mapper = new ObjectMapper();
        //添加自定义信息
        Map<String,String> claims = new HashMap<>();
        String username = ((User)authentication.getPrincipal()).getUsername();
        claims.put(MyEnum.USERNAME.getValue(),username);
        //获取权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> authority = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            authority.add(grantedAuthority.getAuthority());
        }
        String jwt = "";
        try {
            jwt = JWTUtils.createJTW(claims,authority);
        } catch (IOException e) {
            log.error("创建jwt失败！");
            e.printStackTrace();
        }

        httpHeaders.add(HttpHeaders.AUTHORIZATION, jwt);
        wsResponse.setData(authentication.getName());
        try {
            dataBytes=mapper.writeValueAsBytes(wsResponse);
        } catch (JsonProcessingException e) {
            log.error("JSON处理失败！");
            e.printStackTrace();
        }

        HashOperations hashOperations = redisTemplate.opsForHash();

        if (redisTemplate.hasKey(MyEnum.USERNAME.getValue())){
            redisTemplate.delete(MyEnum.USERNAME.getValue());
        }
        hashOperations.put(username,MyEnum.USERNAME.getValue(),username);
        hashOperations.put(username,MyEnum.TOKEN.getValue(),jwt);
        redisTemplate.boundValueOps(MyEnum.USERNAME.getValue()).expire(System.currentTimeMillis() + 120 * 60 * 1000, TimeUnit.MILLISECONDS);
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}
