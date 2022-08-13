package com.guet.ARC.common.exception;

import com.guet.ARC.common.domain.Result;
import com.guet.ARC.common.domain.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlertException extends RuntimeException {

    private int code;

    private String message;

    private boolean success;

    public AlertException(ResultCode resultCode) {
        this.code = resultCode.code();
        this.message = resultCode.message();
        this.success = resultCode.isSuccess();
    }

    public AlertException(int code, String message) {
        this.code = code;
        this.message = message;
        this.success = false;
    }

    public Result<String> fail() {
        return Result.fail(this.code, this.message, this.success);
    }
}
