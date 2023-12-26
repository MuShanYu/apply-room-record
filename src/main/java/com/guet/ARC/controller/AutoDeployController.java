package com.guet.ARC.controller;


import com.alibaba.fastjson.JSON;
import com.guet.ARC.common.anno.ResponseBodyResult;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBodyResult
@Api(tags = "自动部署模块")
@Validated
public class AutoDeployController {

    @PostMapping("/webhook/listener")
    public void receivePush(Object o) {
        System.out.println(JSON.toJSONString(o));
    }
}
