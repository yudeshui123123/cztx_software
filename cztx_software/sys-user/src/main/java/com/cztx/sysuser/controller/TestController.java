package com.cztx.sysuser.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/18 14:36
 * @description:
 */
@RestController
@RefreshScope
public class TestController {

    @Value("${info}")
    private String info;

    @GetMapping("/user/test")
    public String getTest(){
        return info;
    }
}
