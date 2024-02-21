package com.guet.ARC.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.exception.AlertException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
@Slf4j
public class WxUtils {

    private final static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.SECONDS)
            .writeTimeout(500, TimeUnit.SECONDS)
            .readTimeout(500, TimeUnit.SECONDS)
            .callTimeout(500, TimeUnit.SECONDS)
            .build();

    @Data
    public static class WxMessage {
        private String touser;//用户openid
        private String access_token;
        private String template_id;//模版id
        private String page = "pages/index/index";//默认跳到小程序首页
        private String miniprogram_state = "developer";
        private String lang = "zh_CN";
        private Map<String, Map<String, Object>> data;//推送文字
    }

    public static String getOpenid(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=?&secret" +
                "=?&js_code=" + code + "&grant_type=authorization_code&connect_redirect=1";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
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

    private static String getAccessToken() {
        // 获取ACCESS_TOKEN
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" +
                "appid=?&secret=?";
        Request request = new Request.Builder()
                .url(getTokenUrl)
                .get()
                .build();
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

    public static void sendSubscriptionMessage(String openid, String tempId, Map<String, Map<String, Object>> data) {
        String accessToken = getAccessToken();
        String sendMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
        WxMessage wxMessage = new WxMessage();
        wxMessage.setAccess_token(getAccessToken());
        wxMessage.setTouser(openid);
        wxMessage.setTemplate_id(tempId);
        wxMessage.setData(data);
        String postJson = JSON.toJSONString(wxMessage);
        log.info("send message - {}", wxMessage);
        RequestBody requestBody = RequestBody.create(postJson, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(sendMessageUrl)
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
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
}