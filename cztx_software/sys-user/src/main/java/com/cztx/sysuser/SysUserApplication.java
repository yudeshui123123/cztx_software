package com.cztx.sysuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/18 10:30
 * @description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SysUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SysUserApplication.class,args);
    }
}
