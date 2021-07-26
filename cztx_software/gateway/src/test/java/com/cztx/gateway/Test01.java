package com.cztx.gateway;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cztx.common.util.JWTUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/25 15:57
 * @description:
 */
@SpringBootTest
public class Test01 {

    @Test
    public void test01() throws IOException {
        Map<String,String> map = new HashMap<String,String>();
        map.put("username","123");
        String jwt = JWTUtils.createJTW(map);
        DecodedJWT decode = JWTUtils.decode(jwt);
        Map<String, Claim> claims = decode.getClaims();
        System.out.println(123);
    }
}
