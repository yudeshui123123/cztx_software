package com.cztx.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/18 10:44
 * @description:
 */
@Data
@NoArgsConstructor
public class CommonResult<T> {

    private String msg = HttpStatus.OK.reasonPhrase;
    private Integer code = HttpStatus.OK.value;
    private Boolean flag = true;
    private T data;

    public CommonResult(T data){
        this.data = data;
    }

    public CommonResult(Integer code,String msg,Boolean flag){
        this.msg = msg;
        this.code = code;
        this.flag = flag;
    }

    public CommonResult(Integer code,String msg,T data,Boolean flag){
        this.msg = msg;
        this.code = code;
        this.data = data;
        this.flag = flag;
    }

    public static <T> CommonResult <T> builder(){
        return new CommonResult<T>();
    }

    public static <T> CommonResult <T> builder(T data){
        return new CommonResult<T>(data);
    }

    public static <T> CommonResult <T> builder(Integer code,String msg){
        return new CommonResult<T>(code,msg,true);
    }

    public static <T> CommonResult <T> builder(Integer code,String msg,Boolean flag){
        return new CommonResult<T>(code,msg,flag);
    }

    public static <T> CommonResult <T> builder(Integer code,String msg,T data){
        return new CommonResult<T>(code,msg,data,true);
    }

    public static <T> CommonResult <T> builder(Integer code,String msg,T data,Boolean flag){
        return new CommonResult<T>(code,msg,data,flag);
    }

    public static <T> CommonResult <T> bad_request_400_builder(){
        return new CommonResult<T>(HttpStatus.BAD_REQUEST.value,HttpStatus.BAD_REQUEST.reasonPhrase,false);
    }

    public static <T> CommonResult <T> unauthorized_401_builder(){
        return new CommonResult<T>(HttpStatus.UNAUTHORIZED.value,HttpStatus.UNAUTHORIZED.reasonPhrase,false);
    }

    public static <T> CommonResult <T> forbidden_403_builder(){
        return new CommonResult<T>(HttpStatus.FORBIDDEN.value,HttpStatus.FORBIDDEN.reasonPhrase,false);
    }

    public static <T> CommonResult <T> not_found_404_builder(){
        return new CommonResult<T>(HttpStatus.NOT_FOUND.value,HttpStatus.NOT_FOUND.reasonPhrase,false);
    }

    public static <T> CommonResult <T> internal_server_error_500_builder(){
        return new CommonResult<T>(HttpStatus.INTERNAL_SERVER_ERROR.value,HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,false);
    }

    public static <T> CommonResult <T> gateway_timeout_500_builder(){
        return new CommonResult<T>(HttpStatus.GATEWAY_TIMEOUT.value,HttpStatus.GATEWAY_TIMEOUT.reasonPhrase,false);
    }

    public static <T> CommonResult <T> error_response_0_builder(){
        return new CommonResult<T>(HttpStatus.ERROR_RESPONSE.value,HttpStatus.ERROR_RESPONSE.reasonPhrase,false);
    }

    public enum  HttpStatus {
        OK(200, "请求成功"),
        BAD_REQUEST(400, "错误的请求"),
        UNAUTHORIZED(401, "未经授权"),
        FORBIDDEN(403, "禁止访问"),
        NOT_FOUND(404, "未找到资源"),
        INTERNAL_SERVER_ERROR(500, "服务器出现错误"),
        GATEWAY_TIMEOUT(504, "网关超时"),
        ERROR_RESPONSE(0,"未知错误，请联系管理员")
        ;

        private final int value;
        private final String reasonPhrase;

        private HttpStatus(int value, String reasonPhrase) {
            this.value = value;
            this.reasonPhrase = reasonPhrase;
        }

//    public String toString() {
//        return this.value + " " + this.name();
//    }

        public static HttpStatus valueOf(int statusCode) {
            HttpStatus status = resolve(statusCode);
            if (status == null) {
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            } else {
                return status;
            }
        }

        public static HttpStatus resolve(int statusCode) {
            HttpStatus[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                HttpStatus status = var1[var3];
                if (status.value == statusCode) {
                    return status;
                }
            }

            return null;
        }
    }
}
