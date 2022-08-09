package com.guet.ARC.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private boolean success;
    private Integer code;
    private String message;
    private T queryData;

    public Result(ResultCode resultCode, T data){
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.success = resultCode.isSuccess();
        this.queryData = data;
    }

    //成功

    public static <T> Result<T> success(T data){
        return new Result<>(ResultCode.SUCCESS,data);
    }

    //失败
    public static <T> Result<T> failure(ResultCode resultCode, T data) {
        return new Result<T>(resultCode, data);
    }

}
