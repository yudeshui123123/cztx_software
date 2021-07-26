package com.cztx.gateway.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cztx.common.entity.CommonResult;
import com.cztx.common.myenum.MyEnum;
import com.cztx.common.util.JWTUtils;
import com.cztx.gateway.config.RedisLock;
import com.cztx.gateway.utils.JwtErrorResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/19 11:44
 * @description:
 */
@Component
@Slf4j
public class GatewayFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisLock redisLock;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");

        String token = exchange.getRequest().getHeaders().getFirst(MyEnum.TOKEN.getValue());
        //先设置token，后面可能会覆盖。
        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
        DecodedJWT decode = null;
        //判断token是否为空
        if (!StringUtils.isEmpty(token)) {
            //验证jwt
            CommonResult<DecodedJWT> r = JWTUtils.verificationJwt(token);
            if(!r.getFlag()){
                return JwtErrorResult.result(response,r.getMsg());
            }
            decode = r.getData();

            String username = decode.getClaim(MyEnum.USERNAME.getValue()).asString();
            //加锁操作
            String lockStr = null;
            lockStr = redisLock.tryLock(MyEnum.LOCK_NAME.getValue(), 3000);
            try{
                //获取到了锁，则执行正常业务，没有获取到锁，就直接放行
                if(!StringUtils.isEmpty(lockStr)){
                    //1.验证token是否正常，比较redis保存的值，如果不一致，则为无效token，需要重新登录
                    if(redisTemplate.hasKey(username)){
                        if (redisTemplate.boundHashOps(username).hasKey(MyEnum.TOKEN.getValue())) {
                            String redisToken = redisTemplate.boundHashOps(username).get(MyEnum.TOKEN.getValue()).toString();
                            if (!redisToken.equals(token)){
                                return JwtErrorResult.result(response,"您的令牌已经无效，请重新登录");
                            }
                        }else{
                            return JwtErrorResult.result(response,"您的令牌已经无效，请重新登录");
                        }

                    }else{
                        return JwtErrorResult.result(response,"您还没有登录，请登录");
                    }
                    //2.验证token是否需要刷新，如果需要刷新，生成新的令牌，并覆盖redis的token的值
                    if(JWTUtils.checkTokenIsRefresh(token)){
                        //刷新令牌
                        try {
                            String newJTW = JWTUtils.createNewJTW(decode.getClaims());
                            //刷新redis中的值
                            HashOperations hashOperations = redisTemplate.opsForHash();
                            hashOperations.put(username,MyEnum.USERNAME.getValue(),username);
                            hashOperations.put(username,MyEnum.TOKEN.getValue(),newJTW);
                            redisTemplate.boundValueOps(MyEnum.USERNAME.getValue()).expire(System.currentTimeMillis() + 120 * 60 * 1000, TimeUnit.MILLISECONDS);
                            httpHeaders.add(HttpHeaders.AUTHORIZATION, newJTW);
                        } catch (IOException e) {
                            log.error("创建新的令牌时出错！");
                            e.printStackTrace();
                        }
                    }
                }
            }finally {
                //解锁
                if (lockStr != null) {
                    boolean gh = redisLock.unlock(MyEnum.LOCK_NAME.getValue(), token);
                }
            }

        }else{
            return JwtErrorResult.result(response,"请传入令牌");
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
