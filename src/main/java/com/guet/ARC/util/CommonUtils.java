package com.guet.ARC.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.UUID;

public class CommonUtils {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取访问者的ip地址
     * 注：要外网访问才能获取到外网地址，如果你在局域网甚至本机上访问，获得的是内网或者本机的ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            //X-Forwarded-For：Squid 服务代理
            String ipAddresses = request.getHeader("X-Forwarded-For");

            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
                //Proxy-Client-IP：apache 服务代理
                ipAddresses = request.getHeader("Proxy-Client-IP");
            }

            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
                //WL-Proxy-Client-IP：weblogic 服务代理
                ipAddresses = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
                //HTTP_CLIENT_IP：有些代理服务器
                ipAddresses = request.getHeader("HTTP_CLIENT_IP");
            }

            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
                //X-Real-IP：nginx服务代理
                ipAddresses = request.getHeader("X-Real-IP");
            }

            //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
            if (ipAddresses != null && ipAddresses.length() != 0) {
                ipAddress = ipAddresses.split(",")[0];
            }

            //还是不能获取到，最后再通过request.getRemoteAddr();获取
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ipAddress = "";
        }
        return ipAddress;
    }

    public static String getImageSuffix(String suffix) {
        String[] imageSuffix = {"png", "jpg", "bmp", "gif", "tif", "jpeg", "pcx", "PNG", "JPG", "BMP", "GIF", "TIF", "JPEG", "PCX"};
        String str = "jpg";
        for (String s : imageSuffix) {
            boolean contains = suffix.contains(s);
            if (contains) {
                str = s;
                break;
            }
        }
        return str;
    }

    public static boolean isJson(String jsonStr) {
        try {
            JSONObject json = JSONObject.parseObject(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 加密手机号中间四位
    public static String encodeTel(String tel) {
        return tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String encodeName(String name) {
        if (name.length() == 2) {
            return name.replaceFirst(name.substring(1), "*");
        }
        if (name.length() > 2) {
            return name.replaceFirst(name.substring(1, name.length() - 1), "*");
        }
        return name;
    }

    // 获取起始日期的00:00，获取结束日期的11:59:59

    /**
     *
     * @param startTime 起始日期
     * @param endTime 结束日期
     * @return 返回标准起始日期和结束日期00:00:00 ~ 23:59:59，第一个值为起始第二个值为结束
     */
    public static Long[] getStandardStartTimeAndEndTime(Long startTime, Long endTime) {
        // 获取endTime的午夜12点
        Calendar endTimeCalendar = Calendar.getInstance();
        endTimeCalendar.setTimeInMillis(endTime);
        // 设置时间
        endTimeCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endTimeCalendar.set(Calendar.MINUTE, 59);
        endTimeCalendar.set(Calendar.SECOND, 59);
        // 获取这endTime午夜12点的毫秒值
        long webAppDateEnd = endTimeCalendar.getTimeInMillis();
        // 获取startTime的凌晨00：00
        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.setTimeInMillis(startTime);
        startTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startTimeCalendar.set(Calendar.MINUTE, 0);
        startTimeCalendar.set(Calendar.SECOND, 0);
        // startTime的00:00:00
        long webAppDateStart = startTimeCalendar.getTimeInMillis();
        return new Long[]{webAppDateStart, webAppDateEnd};
    }
}
