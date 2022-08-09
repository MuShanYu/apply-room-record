package com.guet.ARC.config;

import com.alibaba.fastjson.JSON;
import com.guet.ARC.common.domain.Result;
import com.guet.ARC.common.exception.AlertException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(AlertException.class)
    public Result<String> alertExceptionHandler(AlertException alertException){
        return alertException.fail();
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> runtimeExceptionHandler(RuntimeException runtimeException) {
        runtimeException.printStackTrace();
        Result<String> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        result.setMessage(runtimeException.getMessage());
        result.setQueryData(null);
        return result;
    }

    @ExceptionHandler(ServletException.class)
    public Result<String> servletExceptionHandler(ServletException servletException) {
        servletException.printStackTrace();
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
        validException.printStackTrace();
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
        validException.printStackTrace();
        Result<String> result = new Result<>();
        result.setCode(5001);
        result.setSuccess(false);
        result.setMessage(validException.getMessage());
        result.setQueryData(null);
        return result;
    }

    @ExceptionHandler(BindException.class)
    public Result<String> constraintValidExceptionHandler(BindException bindException) {
        bindException.printStackTrace();
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
}
