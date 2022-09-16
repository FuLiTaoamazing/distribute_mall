package com.flt.member.exception;

public class PhoneExitsException extends RuntimeException {
    public PhoneExitsException() {
        super("手机号已存在");
    }
}
