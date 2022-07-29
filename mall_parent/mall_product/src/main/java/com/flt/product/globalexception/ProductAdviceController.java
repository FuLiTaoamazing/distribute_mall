package com.flt.product.globalexception;

import com.flt.common.exception.BizCodeEnum;
import com.flt.common.utils.R;
import com.flt.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/*
    集中处理Controller出现的异常比如是Validator校验出错的异常
 */
@RestControllerAdvice(basePackages = "com.flt.product.controller")
public class ProductAdviceController {
    private static final Logger logger = LoggerFactory.getLogger(ProductAdviceController.class);

    // 集中处理Validator数据校验出错的事情
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException exception) {
        logger.error("数据校验出现问题{},异常类型{}", exception.getMessage(), exception.getClass());
        BindingResult bindingResult = exception.getBindingResult();
        Map<String, Object> validField = new HashMap<>();
        bindingResult.getFieldErrors().forEach(item -> {
            validField.put(item.getField(),item.getDefaultMessage());

        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMessage()).put("data", validField);
    }

    // 集中处理业务出现的异常
    @ExceptionHandler(value = ServiceException.class)
    public R handleServiceException(Exception ex) {
        logger.error("业务处理出现异常:{}", ex.getMessage());
        return R.error(501, ex.getMessage());
    }
}
