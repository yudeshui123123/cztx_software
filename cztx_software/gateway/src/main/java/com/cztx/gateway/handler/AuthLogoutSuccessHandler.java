package com.cztx.gateway.handler;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.cztx.common.entity.CommonResult;
import com.cztx.common.myenum.MyEnum;
import com.cztx.common.util.JWTUtils;
import com.cztx.gateway.utils.JwtErrorResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/29 11:14
 * @description:
 */
@Component
@Slf4j
public class AuthLogoutSuccessHandler implements ServerLogoutSuccessHandler{

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        //获取token
        String token = exchange.getRequest().getHeaders().getFirst(MyEnum.TOKEN.getValue());
        DecodedJWT decode = null;
        if (!StringUtils.isEmpty(token)) {
            //验证jwt
            CommonResult<DecodedJWT> r = JWTUtils.verificationJwt(token);
            if(!r.getFlag()){
                return JwtErrorResult.result(response,r.getMsg());
            }
            decode = r.getData();
            String username = decode.getClaim(MyEnum.USERNAME.getValue()).asString();
            //判断redis是否有值
            if (redisTemplate.hasKey(username)) {
                redisTemplate.delete(username);
            }
        }else{
            return JwtErrorResult.result(response,"请传入令牌");
        }
        CommonResult result = CommonResult.builder();
        byte[] dataBytes={};
        ObjectMapper mapper = new ObjectMapper();
        try {
            dataBytes=mapper.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            log.error("JSON处理失败！");
            e.printStackTrace();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}
