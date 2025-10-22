package com.zixi.usermanagementsystem.common;

import com.zixi.usermanagementsystem.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口统一返回对象
 * 接口正常处理时：success: true, data: businessData
 * 接口失败时：success: false, code: errorCode, message: errorMessage
 * @param <T>
 */
@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {

    private boolean success;

    private int code;

    private T data;

    private String message;

    private String description;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, ErrorCode.SUCCESS.getCode(), data, ErrorCode.SUCCESS.getMessage(), "");
    }

    public static <T> BaseResponse<T> fail(ErrorCode errorCode) {
        return new BaseResponse<>(false, errorCode.getCode(), null, errorCode.getMessage(), "");
    }

    public static <T> BaseResponse<T> fail(BusinessException e) {
        return new BaseResponse<>(false, e.getCode(), null, e.getMessage(), e.getDescription());
    }

    public static <T> BaseResponse<T> fail(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(false, errorCode.getCode(), null, message, description);
    }
}
