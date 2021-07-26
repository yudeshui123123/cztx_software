package com.cztx.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cztx.common.entity.CommonResult;
import com.cztx.common.myenum.MyEnum;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;


/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/24 15:13
 * @description:
 */
public class JWTUtils {

    private static PrivateKey PRIVATE_KEY_FILE_RSA;
    private static PublicKey PUBLIC_KEY_FILE_RSA;

    private static final String ISS = "cztx";
    private static final long EXP = 120 * 60 * 1000;
    private static final long REFRESH_CHECK_TIME = 20 * 60 * 1000;
    private static Algorithm ALGORITHM = null;


    static {
        try {
            PRIVATE_KEY_FILE_RSA = PemUtils.readPrivateKeyFromFile(Thread.currentThread().getContextClassLoader().getResource("key/rsa-private.pem").getPath(), "RSA");
            PUBLIC_KEY_FILE_RSA = PemUtils.readPublicKeyFromFile(Thread.currentThread().getContextClassLoader().getResource("key/rsa-public.pem").getPath(), "RSA");
            ALGORITHM = Algorithm.RSA256((RSAPublicKey) PUBLIC_KEY_FILE_RSA, (RSAPrivateKey) PRIVATE_KEY_FILE_RSA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JWTUtils() {
    }

    /**
     * 生成默认jwt
     *
     * @param claims
     * @return
     * @throws IOException
     */
    public static String createJTW(Map<String, String> claims) throws IOException {
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(ISS)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP));
        for (String key : claims.keySet()) {
            builder.withClaim(key, claims.get(key));
        }
        builder.withClaim("refreshCheckTime", System.currentTimeMillis() + REFRESH_CHECK_TIME);
        return builder.sign(ALGORITHM);
    }

    /**
     * 创建带权限信息的jwt
     *
     * @param var1
     * @param var2
     * @return
     * @throws IOException
     */
    public static String createJTW(Map<String, String> var1, List<String> var2) throws IOException {
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(ISS)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP));
        for (String key : var1.keySet()) {
            builder.withClaim(key, var1.get(key));
        }
        builder.withClaim("authority", var2);
        builder.withClaim("refreshCheckTime", System.currentTimeMillis() + REFRESH_CHECK_TIME);
        return builder.sign(ALGORITHM);
    }

    /**
     * 创建刷新的的jwt
     *
     * @param map
     * @return
     * @throws IOException
     */
    public static String createNewJTW(Map<String, Claim> map) throws IOException {
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(ISS)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP));
        builder.withClaim("authority", map.get("authority").asList(String.class));
        builder.withClaim(MyEnum.USERNAME.getValue(), map.get(MyEnum.USERNAME.getValue()).asString());
        builder.withClaim("refreshCheckTime", System.currentTimeMillis() + REFRESH_CHECK_TIME);
        return builder.sign(ALGORITHM);
    }

    /**
     * 生成自定义参数jwt
     * @param claims
     * @param issuer
     * @param timeout
     * @return
     * @throws IOException
     */
    /**
     * @deprecated
     */
    public static String createJTW(Map<String, String> claims, String issuer, long timeout) throws IOException {
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(issuer)
                .withExpiresAt(new Date((System.currentTimeMillis() + timeout)));
        for (String key : claims.keySet()) {
            builder.withClaim(key, claims.get(key));
        }
        builder.withClaim("refreshCheckTime", System.currentTimeMillis() + REFRESH_CHECK_TIME);
        return builder.sign(ALGORITHM);
    }

    /**
     * 解析默认的jwt
     *
     * @param token
     * @return
     */
    public static DecodedJWT decode(String token) throws JWTVerificationException {
        DecodedJWT jwt = null;
        JWTVerifier verifier = JWT.require(ALGORITHM)
                .withIssuer(ISS)
                .build();
        jwt = verifier.verify(token);
        return jwt;
    }

    /**
     * @deprecated
     */
    public static DecodedJWT decode(String token, String iss) throws JWTVerificationException {
        DecodedJWT jwt = null;
        JWTVerifier verifier = JWT.require(ALGORITHM)
                .withIssuer(iss)
                .build();
        jwt = verifier.verify(token);
        return jwt;
    }

    /**
     * 验证令牌是否需要刷新
     *
     * @param token
     * @return true 需要刷新，false 不需要刷新
     */
    public static Boolean checkTokenIsRefresh(String token) {
        DecodedJWT decode = decode(token);
        Long refreshCheckTime = decode.getClaim("refreshCheckTime").asLong();
        //如果小于等于当前时间，返回true
        if (System.currentTimeMillis() >= refreshCheckTime) {
            return true;
        }
        return false;
    }

    public static CommonResult<DecodedJWT> verificationJwt(String token) {
        CommonResult<DecodedJWT> result = new CommonResult();
        result.setFlag(false);
        DecodedJWT decode = null;
        //验证jwt
        try {
            decode = decode(token);
        } catch (TokenExpiredException e) {
            result.setMsg("令牌过期,请重新登录");
        } catch (AlgorithmMismatchException e) {
            result.setMsg("令牌算法异常");
        } catch (SignatureVerificationException e) {
            result.setMsg("验证签名失败");
        } catch (InvalidClaimException e) {
            result.setMsg("验证令牌内容失败");
        } catch (JWTDecodeException e){
            result.setMsg("请传入正确的令牌");
        }catch (Exception e) {
            result.setMsg("未知错误，请重新登录");
        }
        if (decode != null) {
            result.setFlag(true);
            result.setData(decode);
        }

        return result;
    }
}
