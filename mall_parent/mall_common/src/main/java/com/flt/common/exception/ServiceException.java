package com.flt.common.exception;
//业务处理的时候遇到的异常
public class ServiceException  extends RuntimeException{

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }
}
