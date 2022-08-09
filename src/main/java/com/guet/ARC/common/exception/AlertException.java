package com.guet.ARC.common.exception;

import com.guet.ARC.common.domain.Result;
import com.guet.ARC.common.domain.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlertException extends RuntimeException {

    private ResultCode resultCode;

    public AlertException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Result<String> fail() {
        return Result.failure(this.resultCode, getMessage());
    }
}
