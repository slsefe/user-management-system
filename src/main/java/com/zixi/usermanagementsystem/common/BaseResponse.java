package com.zixi.usermanagementsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(1, data, "ok");
    }

    public static <T> BaseResponse<T> fail(String message) {
        return new BaseResponse<>(0, null, message);
    }
}
