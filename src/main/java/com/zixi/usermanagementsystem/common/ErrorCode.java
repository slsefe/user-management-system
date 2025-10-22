package com.zixi.usermanagementsystem.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(20000, "ok"),
    PARAMS_ERROR(40000, "request param error"),
    NULL_ERROR(40001, "request param is null"),
    NO_LOGIN(40100, "no login"),
    NO_PERMISSION(40101, "no permission"),
    SYSTEM_ERROR(50000, "");

    private final int code;
    private final String message;
}
