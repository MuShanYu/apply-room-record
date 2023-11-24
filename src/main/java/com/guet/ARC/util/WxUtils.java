package com.guet.ARC.util;


import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.exception.AlertException;
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
/**
 * Author: Yulf
 * Date: 2023/11/21
 */
@Slf4j
public class WxUtils {



    public static String getOpenid(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wx3cdb834f59308215&secret" +
                "=b9cb7ee08766923b459306233996ad3e&js_code=" + code + "&grant_type=authorization_code&connect_redirect=1";
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

//    public static void main(String[] args) {
//        MessageDTO build = MessageDTO.builder()
//                .thing21("办事事项审核通知")
//                .date4("2022-04-19")
//                .name1("于林峰")
//                .phrase2("通过")
//                .thing3("进入小程序查看审核详情信息")
//                .build();
//        sendSubscriptionMessage("ouN8v5HpNgLZpoytBOL560h47sws", build);
//    }

    /*public static void sendSubscriptionMessage(String openid, MessageDTO messageDTO) {
        String accessToken = getAccessToken();
        String sendMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
        MessageTemplate messageTemplate = MessageTemplate.builder()
                .access_token(getAccessToken())
                .touser(openid)
                .template_id("Mxo34TDWXM2byXwSbX1tDcKvbaUlTiuc_bzxAoiehc4")
                .page("pages/home/index")
                .miniprogram_state("developer")
                .lang("zh_CN")
                .data(
                        MessageDataTemplate.builder()
                                .thing21(MessageDataValueTemplate.builder().value(messageDTO.getThing21()).build())
                                .date4(MessageDataValueTemplate.builder().value(messageDTO.getDate4()).build())
                                .name1(MessageDataValueTemplate.builder().value(messageDTO.getName1()).build())
                                .phrase2(MessageDataValueTemplate.builder().value(messageDTO.getPhrase2()).build())
                                .thing3(MessageDataValueTemplate.builder().value(messageDTO.getThing3()).build())
                                .build()
                ).build();
        String postJson = JSON.toJSONString(messageTemplate);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(sendMessageUrl);
        CloseableHttpResponse response = null;
        try {
            StringEntity entity = new StringEntity(postJson, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            response = httpClient.execute(httpPost);
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
                e.printStackTrace();
            }
        }
    }*/

    /*private static String getAccessToken() {
        // 获取ACCESS_TOKEN
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxdb9e3440c6290f03&secret=5c343482284c13b7f24c955ab885f9cf";
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
                e.printStackTrace();
            }
        }
        return null;
    }*/
}