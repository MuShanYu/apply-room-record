package com.guet.ARC.config;

import cn.dev33.satoken.exception.NotLoginException;
import com.alibaba.fastjson.JSON;
import com.guet.ARC.common.domain.Result;
import com.guet.ARC.common.exception.AlertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 线上环境请注释printStackTrace
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(AlertException.class)
    public Result<String> alertExceptionHandler(AlertException alertException){
        return alertException.fail();
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> runtimeExceptionHandler(RuntimeException runtimeException) {
        log.error("运行时异常：", runtimeException);
        Result<String> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage(runtimeException.getMessage());
        result.setQueryData(null);
        return result;
    }

    @ExceptionHandler(ServletException.class)
    public Result<String> servletExceptionHandler(ServletException servletException) {
        log.error("ServletException异常：", servletException);
        Result<String> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage(servletException.getMessage());
        result.setQueryData(null);
        return result;
    }

    // 参数校验错误处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> argumentNotValidExceptionHandler(MethodArgumentNotValidException validException) {
//        validException.printStackTrace();
        BindingResult exceptionBindingResult = validException.getBindingResult();
        Map<String, String> map = new HashMap<>();
        // 获取校验结果，遍历获取捕获到的每个校验结果
        exceptionBindingResult.getFieldErrors().forEach(item ->{
            // 存储得到的校验结果
            map.put(item.getField(), item.getDefaultMessage());
        });
        Result<String> result = new Result<>();
        result.setCode(5001);
        result.setSuccess(false);
        result.setMessage(JSON.toJSONString(map));
        result.setQueryData(null);
        return result;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> constraintValidExceptionHandler(ConstraintViolationException validException) {
//        validException.printStackTrace();
        Result<String> result = new Result<>();
        result.setCode(5001);
        result.setSuccess(false);
        result.setMessage(validException.getMessage());
        result.setQueryData(null);
        return result;
    }

    @ExceptionHandler(BindException.class)
    public Result<String> constraintValidExceptionHandler(BindException bindException) {
//        bindException.printStackTrace();
        BindingResult bindingResult = bindException.getBindingResult();
        Map<String, String> map = new HashMap<>();
        // 获取校验结果，遍历获取捕获到的每个校验结果
        bindingResult.getFieldErrors().forEach(item ->{
            // 存储得到的校验结果
            map.put(item.getField(), item.getDefaultMessage());
        });
        Result<String> result = new Result<>();
        result.setCode(5001);
        result.setSuccess(false);
        result.setMessage(JSON.toJSONString(map));
        result.setQueryData(null);
        return result;
    }

    @ExceptionHandler(NotLoginException.class)
    public Result<String> handleNotLoginException(NotLoginException e) {
//        e.printStackTrace();
        String message = "";
        int code = 0;
        if(e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "请先登录";
            code = -1;
        }
        else if(e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "令牌无效";
            code = -2;
        }
        else if(e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "令牌已过期";
            code = -3;
        }
        else if(e.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "a";
            code = -4;
        }
        else if(e.getType().equals(NotLoginException.KICK_OUT)) {
            message = "您已被踢下线";
            code = -5;
        }
        else {
            message = "当前会话未登录";
            code = 2001;
        }
        Result<String> result = new Result<>();
        result.setCode(code);
        result.setSuccess(false);
        result.setMessage(message);
        result.setQueryData(null);
        return result;
    }
}
