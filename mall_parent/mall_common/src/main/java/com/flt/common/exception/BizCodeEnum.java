package com.flt.common.exception;

// 分布式项目统一错误代码
public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000, "系统未知错误"),
    VALID_EXCEPTION(10001, "参数校验出错"),
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