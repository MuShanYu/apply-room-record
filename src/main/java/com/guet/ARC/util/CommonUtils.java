package com.guet.ARC.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


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
     * @param startTime 起始日期
     * @param endTime   结束日期
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

    public static Map<String, Object> createValueItem(String value) {
        HashMap<String, Object> objectHashMap = new HashMap<>(1);
        objectHashMap.put("value", value);
        return objectHashMap;
    }

    public static boolean isValidMail(String mail) {
        if (mail == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(mail);
        return matcher.matches();
    }


    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return "";
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    public static HttpServletRequest getContextRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

}
