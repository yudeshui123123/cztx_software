package com.cztx.gateway.handler;

import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/23 16:06
 * @description:
 * 异常拦截器
 */
public class GlobalExceptionHandler implements WebExceptionHandler, Ordered {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        System.out.println(123);
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
