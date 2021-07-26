package com.cztx.common.myenum;

/**
 * TODO
 *
 * @author yds
 * @version 1.0
 * @date 2021/3/27 11:56
 * @description:
 */
public enum MyEnum {

    USERNAME("username"),
    TOKEN("token"),
    LOCK_NAME("lock_token");

    private final String value;

    MyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
