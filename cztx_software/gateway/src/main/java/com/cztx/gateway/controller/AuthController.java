package com.cztx.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/17 12:05
 * @description:
 */
@RestController
@RefreshScope
public class AuthController {

    @Value("${config.info}")
    public String info;

    @GetMapping("/test")
    public String getTest(){
        return info;
    }
}
