package com.guet.ARC.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {

    private boolean success;
    private Integer code;
    private String message;
    private T queryData;

    public Result() {
    }

    public Result(ResultCode resultCode, T data){
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.success = resultCode.isSuccess();
        this.queryData = data;
    }

    public Result(Integer code, String message, boolean success){
        this.code = code;
        this.message = message;
        this.success = success;
        this.queryData = null;
    }

    //成功

    public static <T> Result<T> success(T data){
        return new Result<>(ResultCode.SUCCESS,data);
    }

    //失败
    public static <T> Result<T> fail(ResultCode resultCode, T data) {
        return new Result<T>(resultCode, data);
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setQueryData(null);
        return result;
    }

    public static <T> Result<T> fail(int code, String message, boolean success) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(success);
        result.setQueryData(null);
        return result;
    }

}
