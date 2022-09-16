package com.flt.common.exception;

// 分布式项目统一错误代码
public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000, "系统未知错误"),
    VALID_EXCEPTION(10001, "参数校验出错"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高请稍后再试"),
    USER_EXITS_EXCEPTION(150001, "用户名已存在"),
    PHONE_EXITS_EXCEPTION(150002, "手机号已存在"),
    LOGINACCOUNT_PASSWORD_INVALID_EXCEPTION(150003, "用户名或密码错误"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架出错");


    private Integer code;
    private String message;

    BizCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}