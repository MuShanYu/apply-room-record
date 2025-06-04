package top.mushanyu.config.session;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static top.mushanyu.config.session.SessionKeys.*;

@Slf4j
public class AppSession {

    private static final ThreadLocal<Map<String, Object>> SESSION_ATTRIBUTE = ThreadLocal.withInitial(HashMap::new);

    public static Long getUserId() {
        return convert(get(APP_USER_ID), c -> Long.valueOf(c.toString()));
    }

    public static void setUserId(Long userId) {
        add(APP_USER_ID, userId);
    }

    public static String getLanguage() {
        return convert(getOrDefault(APP_LAN, "cn"), Object::toString);
    }

    public static void setLanguage(String language) {
        add(APP_LAN, language);
    }

    // 不在此判断能转换的目标类型，谁增加谁负责准确转换，注意空处理
    protected static  <T> T convert(Object obj, Function<Object, T> convert) {
        if (obj == null) {
            return null;
        }
        return convert.apply(obj);
    }

    public static void putAll(Map<String, Object> maps) {
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            if (StrUtil.toLoweCase(entry.getKey()).startsWith(KEY_PREFIX)) {
                SESSION_ATTRIBUTE.get().put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static Map<String, Object> getAll() {
        return SESSION_ATTRIBUTE.get();
    }

    private static void add(String key, Object value) {
        SESSION_ATTRIBUTE.get().put(key, value);
    }

    private static Object get(String key) {
        return SESSION_ATTRIBUTE.get().get(key);
    }

    private static Object getOrDefault(String key, Object dV) {
        return SESSION_ATTRIBUTE.get().getOrDefault(key, dV);
    }

    public static void clear() {
        SESSION_ATTRIBUTE.remove();
    }

}
