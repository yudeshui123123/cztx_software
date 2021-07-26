package com.cztx.gateway.utils;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cztx.common.entity.CommonResult;
import com.cztx.common.util.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/29 11:30
 * @description:
 */
@Slf4j
public class JwtErrorResult {

    public static Mono<Void> result(ServerHttpResponse response, String message){
        byte[] dataBytes= {};
        CommonResult<String> commonResult = new CommonResult<String>();
        commonResult.setCode(401);
        commonResult.setMsg(message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            dataBytes=mapper.writeValueAsBytes(commonResult);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}
