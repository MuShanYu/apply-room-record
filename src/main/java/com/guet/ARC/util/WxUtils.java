package com.guet.ARC.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.enums.ReservationState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
@Slf4j
public class WxUtils {

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
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取请求体内容
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                return jsonObject.getString("openid");
            }
        } catch (Exception e) {
            log.error("get openid failed.", e);
            throw new AlertException(1000, "用户微信标识获取失败.");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                //相当于关闭浏览器
                httpClient.close();
            } catch (IOException e) {
                log.error("Filed.", e);
            }
        }
        return null;
    }

    private static String getAccessToken() {
        // 获取ACCESS_TOKEN
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" +
                "appid=?&secret=?";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(getTokenUrl);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取请求体内容
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                return jsonObject.getString("access_token");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                //相当于关闭浏览器
                httpClient.close();
            } catch (IOException e) {
                log.error("get access token error:", e);
            }
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
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(sendMessageUrl);
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(postJson, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            response = httpClient.execute(httpPost);
            log.info("send message response - {}", JSON.toJSONString(response));
            if (response.getStatusLine().getStatusCode() != 200) {
                // 获取请求体内容
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                //相当于关闭浏览器
                httpClient.close();
            } catch (IOException e) {
                log.error("send message：", e);
            }
        }
    }
}