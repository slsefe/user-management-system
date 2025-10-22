package com.zixi.usermanagementsystem.exception;

import com.zixi.usermanagementsystem.common.BaseResponse;
import com.zixi.usermanagementsystem.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public <T> BaseResponse<T> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException: " + e.getMessage(), e);
        return BaseResponse.fail(e);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public <T> BaseResponse<T> exceptionHandler(RuntimeException e) {
        log.error("RuntimeException: " + e.getMessage(), e);
        return BaseResponse.fail(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }

}
