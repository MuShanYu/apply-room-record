package com.guet.ARC.util;


import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.ApplyRoomRecordConfig;
import com.guet.ARC.common.exception.AlertException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
@Slf4j
public class WxUtils {

    private String appId;

    private String secret;

    private OkHttpClient okHttpClient;

    private static WxUtils wxUtils;

    private WxUtils() {
        init();
    }

    private void init() {
        ApplyRoomRecordConfig globalConfig = SpringUtil.getBean("applyRoomRecordConfig", ApplyRoomRecordConfig.class);
        appId = globalConfig.getAppId();
        secret = globalConfig.getSecret();
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.SECONDS)
                .writeTimeout(500, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.SECONDS)
                .callTimeout(500, TimeUnit.SECONDS)
                .build();
    }

    public static WxUtils getInstance() {
        if (wxUtils == null) {
            wxUtils = new WxUtils();
        }
        return wxUtils;
    }

    @Data
    public static class WxMessage {
        private String touser;//用户openid
        private String access_token;
        private String template_id;//模版id
        private String page = "pages/index/index";//默认跳到小程序首页
        private String miniprogram_state = "formal";
        private String lang = "zh_CN";
        private Map<String, Map<String, Object>> data;//推送文字
    }

    public String getOpenid(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+ appId +
                "&secret=" + secret + "&js_code=" + code
                + "&grant_type=authorization_code&connect_redirect=1";
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                return jsonObject.getString("openid");
            }
        } catch (IOException e) {
            log.error("get openid failed.", e);
            throw new AlertException(1000, "用户微信标识获取失败.");
        }
        return null;
    }

    private String getAccessToken() {
        // 获取ACCESS_TOKEN
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&"
                + "appid=" + appId + "&secret=" + secret;
        Request request = new Request.Builder().url(getTokenUrl).get().build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                // 相应成功
                if (response.body() != null) {
                    String res = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(res);
                    return jsonObject.getString("access_token");
                }
            } else {
                // 响应失败
                log.error("request failed. case there is no response.{}", response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void sendSubscriptionMessage(String openid, String tempId, Map<String, Map<String, Object>> data) {
        String sendMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + getAccessToken();
        WxMessage wxMessage = new WxMessage();
        wxMessage.setAccess_token(getAccessToken());
        wxMessage.setTouser(openid);
        wxMessage.setTemplate_id(tempId);
        wxMessage.setData(data);
        String postJson = JSON.toJSONString(wxMessage);
        log.info("send message - {}", wxMessage);
        RequestBody requestBody = RequestBody.create(postJson, MediaType.get("application/json"));
        Request request = new Request.Builder().url(sendMessageUrl).post(requestBody).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    log.info("send message response - {}", response.body().string());
                } else {
                    log.warn("message send failed.");
                }
            }
        } catch (IOException e) {
            log.error("request failed.", e);
        }
    }

    public byte[] createWxQRCode(String roomId) {
        String postUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + getAccessToken();
        JSONObject postData = new JSONObject();
        postData.put("page", "page-work/sign-in/index");
        postData.put("scene", roomId);
        postData.put("env_version", "release");
        RequestBody requestBody = RequestBody.create(postData.toJSONString(), MediaType.get("application/json"));
        Request request = new Request.Builder().url(postUrl).post(requestBody).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (Objects.equals(response.header("Content-Type"), "image/jpeg")) {
                        return response.body().bytes();
                    } else {
                        log.error("二维码请求失败：{}", JSONObject.toJSONString(response.body().string()));
                    }
                }
            } else {
                log.info("create qr code failed. the error response is {}", response.body());
            }
        } catch (IOException e) {
            log.error("request failed.", e);
        }
        return new byte[0];
    }
}